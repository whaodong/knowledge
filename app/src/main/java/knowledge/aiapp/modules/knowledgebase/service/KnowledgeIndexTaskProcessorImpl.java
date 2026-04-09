package knowledge.aiapp.modules.knowledgebase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import knowledge.aiapp.infrastructure.file.FilePreprocessService;
import knowledge.aiapp.infrastructure.redis.KnowledgeTaskStreamProducer;
import knowledge.aiapp.infrastructure.vector.VectorInfrastructureService;
import knowledge.aiapp.common.enums.ResultCodeEnum;
import knowledge.aiapp.common.exception.BusinessException;
import knowledge.aiapp.modules.knowledgebase.constant.KnowledgeConstants;
import knowledge.aiapp.modules.knowledgebase.converter.KnowledgebaseConverter;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeChunkEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeDocumentEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeIndexTaskEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgebaseEntity;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeChunkRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeDocumentRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeIndexTaskRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgebaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 知识索引任务处理实现。
 */
@Slf4j
@Service
public class KnowledgeIndexTaskProcessorImpl implements KnowledgeIndexTaskProcessor {

    private final KnowledgeIndexTaskRepository taskRepository;
    private final KnowledgeDocumentRepository documentRepository;
    private final KnowledgeChunkRepository chunkRepository;
    private final KnowledgebaseRepository knowledgebaseRepository;
    private final FilePreprocessService filePreprocessService;
    private final VectorInfrastructureService vectorInfrastructureService;
    private final KnowledgeTaskStreamProducer taskStreamProducer;
    private final KnowledgebaseService knowledgebaseService;
    private final KnowledgebaseConverter knowledgebaseConverter;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public KnowledgeIndexTaskProcessorImpl(KnowledgeIndexTaskRepository taskRepository,
                                           KnowledgeDocumentRepository documentRepository,
                                           KnowledgeChunkRepository chunkRepository,
                                           KnowledgebaseRepository knowledgebaseRepository,
                                           FilePreprocessService filePreprocessService,
                                           VectorInfrastructureService vectorInfrastructureService,
                                           KnowledgeTaskStreamProducer taskStreamProducer,
                                           KnowledgebaseService knowledgebaseService,
                                           KnowledgebaseConverter knowledgebaseConverter,
                                           ObjectMapper objectMapper,
                                           TransactionTemplate transactionTemplate) {
        this.taskRepository = taskRepository;
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.knowledgebaseRepository = knowledgebaseRepository;
        this.filePreprocessService = filePreprocessService;
        this.vectorInfrastructureService = vectorInfrastructureService;
        this.taskStreamProducer = taskStreamProducer;
        this.knowledgebaseService = knowledgebaseService;
        this.knowledgebaseConverter = knowledgebaseConverter;
        this.objectMapper = objectMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void processTask(Long taskId) {
        try {
            transactionTemplate.executeWithoutResult(status -> doProcessTask(taskId));
        } catch (Exception ex) {
            handleTaskFailure(taskId, ex);
        }
    }

    protected void doProcessTask(Long taskId) {
        KnowledgeIndexTaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "索引任务不存在"));
        if (KnowledgeConstants.TASK_STATUS_SUCCESS.equals(task.getStatus())) {
            log.info("索引任务已完成，跳过重复处理, taskId={}", taskId);
            return;
        }

        task.setStatus(KnowledgeConstants.TASK_STATUS_PROCESSING);
        task.setStartedAt(LocalDateTime.now());
        task.setErrorMessage(null);
        taskRepository.save(task);

        KnowledgeDocumentEntity document = documentRepository.findById(task.getDocumentId())
                .orElseThrow(() -> new IllegalStateException("文档不存在"));
        KnowledgebaseEntity knowledgebase = knowledgebaseRepository.findById(task.getKnowledgeBaseId())
                .orElseThrow(() -> new IllegalStateException("知识库不存在"));

        document.setStatus(KnowledgeConstants.DOC_STATUS_PARSING);
        documentRepository.save(document);

        String content = filePreprocessService.extractText(Path.of(document.getStorageKey()), document.getFileExt());
        document.setCharCount(content.length());
        document.setStatus(KnowledgeConstants.DOC_STATUS_PARSED);
        documentRepository.save(document);

        List<String> chunks = filePreprocessService.splitText(content);
        chunkRepository.deleteByDocumentId(document.getId());
        document.setStatus(KnowledgeConstants.DOC_STATUS_CHUNKED);
        document.setChunkCount(chunks.size());
        documentRepository.save(document);

        document.setStatus(KnowledgeConstants.DOC_STATUS_VECTORIZING);
        documentRepository.save(document);

        for (int i = 0; i < chunks.size(); i++) {
            String chunkContent = chunks.get(i);
            KnowledgeChunkEntity chunkEntity = new KnowledgeChunkEntity();
            chunkEntity.setKnowledgeBaseId(knowledgebase.getId());
            chunkEntity.setDocumentId(document.getId());
            chunkEntity.setChunkNo(i + 1);
            chunkEntity.setContent(chunkContent);
            chunkEntity.setContentPreview(knowledgebaseConverter.buildPreview(chunkContent));
            chunkEntity.setTokenCount(knowledgebaseConverter.estimateTokenCount(chunkContent));
            chunkEntity.setCharCount(chunkContent.length());
            chunkEntity.setEnabled(true);
            chunkEntity.setMetadataJson(buildChunkMetadataJson(knowledgebase, document, i + 1));
            KnowledgeChunkEntity savedChunk = chunkRepository.save(chunkEntity);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("knowledgeBaseId", knowledgebase.getId());
            metadata.put("documentId", document.getId());
            metadata.put("chunkId", savedChunk.getId());
            metadata.put("chunkNo", savedChunk.getChunkNo());
            metadata.put("documentName", document.getFileName());
            vectorInfrastructureService.addChunkDocument(String.valueOf(savedChunk.getId()), chunkContent, metadata);
            savedChunk.setVectorRefId(String.valueOf(savedChunk.getId()));
            chunkRepository.save(savedChunk);
        }

        document.setStatus(KnowledgeConstants.DOC_STATUS_COMPLETED);
        document.setErrorMessage(null);
        documentRepository.save(document);

        task.setStatus(KnowledgeConstants.TASK_STATUS_SUCCESS);
        task.setFinishedAt(LocalDateTime.now());
        taskRepository.save(task);

        knowledgebase.setLastIndexedAt(LocalDateTime.now());
        knowledgebaseRepository.save(knowledgebase);
        knowledgebaseService.refreshKnowledgeBaseStats(knowledgebase.getId());
        log.info("索引任务处理成功, taskId={}, documentId={}", taskId, document.getId());
    }

    private void handleTaskFailure(Long taskId, Exception ex) {
        transactionTemplate.executeWithoutResult(status -> {
            KnowledgeIndexTaskEntity task = taskRepository.findById(taskId).orElse(null);
            if (task == null) {
                log.error("索引任务失败且任务记录不存在, taskId={}", taskId, ex);
                return;
            }
            task.setRetryCount(task.getRetryCount() == null ? 1 : task.getRetryCount() + 1);
            task.setFinishedAt(LocalDateTime.now());
            task.setErrorMessage(ex.getMessage());

            KnowledgeDocumentEntity document = documentRepository.findById(task.getDocumentId()).orElse(null);
            if (document != null) {
                document.setStatus(KnowledgeConstants.DOC_STATUS_FAILED);
                document.setErrorMessage(ex.getMessage());
                documentRepository.save(document);
            }

            if (task.getRetryCount() < task.getMaxRetryCount()) {
                task.setStatus(KnowledgeConstants.TASK_STATUS_PENDING);
                taskRepository.save(task);
                taskStreamProducer.sendTask(task.getId());
                log.error("索引任务失败后重试, taskId={}, retry={}", task.getId(), task.getRetryCount(), ex);
            } else {
                task.setStatus(KnowledgeConstants.TASK_STATUS_FAILED);
                taskRepository.save(task);
                log.error("索引任务失败并超过重试次数, taskId={}", task.getId(), ex);
            }
        });
    }

    private String buildChunkMetadataJson(KnowledgebaseEntity knowledgebase,
                                          KnowledgeDocumentEntity document,
                                          int chunkNo) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("knowledgeBaseId", knowledgebase.getId());
        metadata.put("documentId", document.getId());
        metadata.put("documentName", document.getFileName());
        metadata.put("chunkNo", chunkNo);
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}

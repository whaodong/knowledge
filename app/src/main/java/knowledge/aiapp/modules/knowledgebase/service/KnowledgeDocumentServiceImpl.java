package knowledge.aiapp.modules.knowledgebase.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import knowledge.aiapp.common.enums.ResultCodeEnum;
import knowledge.aiapp.common.exception.BusinessException;
import knowledge.aiapp.infrastructure.file.FilePreprocessService;
import knowledge.aiapp.infrastructure.redis.KnowledgeTaskStreamProducer;
import knowledge.aiapp.infrastructure.storage.StorageService;
import knowledge.aiapp.modules.knowledgebase.constant.KnowledgeConstants;
import knowledge.aiapp.modules.knowledgebase.converter.KnowledgebaseConverter;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeDocumentEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeIndexTaskEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgebaseEntity;
import knowledge.aiapp.modules.knowledgebase.dto.request.KnowledgeDocumentUploadRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeChunkResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeDocumentResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeIndexTaskResponse;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeChunkRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeDocumentRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeIndexTaskRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgebaseRepository;
import knowledge.aiapp.modules.user.domain.User;
import knowledge.aiapp.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 知识文档服务实现。
 */
@Slf4j
@Service
public class KnowledgeDocumentServiceImpl implements KnowledgeDocumentService {

    private final KnowledgebaseRepository knowledgebaseRepository;
    private final KnowledgeDocumentRepository documentRepository;
    private final KnowledgeChunkRepository chunkRepository;
    private final KnowledgeIndexTaskRepository taskRepository;
    private final FilePreprocessService filePreprocessService;
    private final StorageService storageService;
    private final KnowledgeTaskStreamProducer taskStreamProducer;
    private final KnowledgebaseConverter knowledgebaseConverter;
    private final KnowledgebaseService knowledgebaseService;
    private final UserRepository userRepository;

    public KnowledgeDocumentServiceImpl(KnowledgebaseRepository knowledgebaseRepository,
                                        KnowledgeDocumentRepository documentRepository,
                                        KnowledgeChunkRepository chunkRepository,
                                        KnowledgeIndexTaskRepository taskRepository,
                                        FilePreprocessService filePreprocessService,
                                        StorageService storageService,
                                        KnowledgeTaskStreamProducer taskStreamProducer,
                                        KnowledgebaseConverter knowledgebaseConverter,
                                        KnowledgebaseService knowledgebaseService,
                                        UserRepository userRepository) {
        this.knowledgebaseRepository = knowledgebaseRepository;
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.taskRepository = taskRepository;
        this.filePreprocessService = filePreprocessService;
        this.storageService = storageService;
        this.taskStreamProducer = taskStreamProducer;
        this.knowledgebaseConverter = knowledgebaseConverter;
        this.knowledgebaseService = knowledgebaseService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeDocumentResponse uploadDocument(Long knowledgeBaseId, KnowledgeDocumentUploadRequest request) {
        KnowledgebaseEntity knowledgebase = getKnowledgeBase(knowledgeBaseId);
        try {
            filePreprocessService.validateFile(request.getFile());
            String fileExt = filePreprocessService.detectFileType(request.getFile());
            String storageKey = storageService.save(request.getFile(), "knowledgebase/" + knowledgeBaseId);

            KnowledgeDocumentEntity document = new KnowledgeDocumentEntity();
            document.setKnowledgeBaseId(knowledgeBaseId);
            document.setFileName(request.getFile().getOriginalFilename());
            document.setFileExt(fileExt);
            document.setContentType(request.getFile().getContentType());
            document.setStorageKey(storageKey);
            document.setFileSize(request.getFile().getSize());
            document.setStatus(KnowledgeConstants.DOC_STATUS_UPLOADED);
            document.setParserType(fileExt);
            document.setChunkCount(0);
            document.setUploadedBy(getCurrentUserId());
            document.setDeleted(false);
            KnowledgeDocumentEntity saved = documentRepository.save(document);

            KnowledgeIndexTaskEntity task = createIndexTask(knowledgebase, saved, "upload");
            publishTaskAfterCommit(task.getId());
            knowledgebaseService.refreshKnowledgeBaseStats(knowledgeBaseId);
            log.info("文档上传成功并创建索引任务, knowledgeBaseId={}, documentId={}, taskId={}",
                    knowledgeBaseId, saved.getId(), task.getId());
            return knowledgebaseConverter.toKnowledgeDocumentResponse(saved);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "文档上传失败: " + ex.getMessage());
        }
    }

    @Override
    public List<KnowledgeDocumentResponse> listDocuments(Long knowledgeBaseId) {
        getKnowledgeBase(knowledgeBaseId);
        return documentRepository.findByKnowledgeBaseIdAndDeletedFalseOrderByCreatedAtDesc(knowledgeBaseId).stream()
                .map(knowledgebaseConverter::toKnowledgeDocumentResponse)
                .toList();
    }

    @Override
    public KnowledgeDocumentResponse getDocument(Long knowledgeBaseId, Long documentId) {
        getKnowledgeBase(knowledgeBaseId);
        return knowledgebaseConverter.toKnowledgeDocumentResponse(getDocumentEntity(knowledgeBaseId, documentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long knowledgeBaseId, Long documentId) {
        KnowledgeDocumentEntity entity = getDocumentEntity(knowledgeBaseId, documentId);
        entity.setDeleted(true);
        entity.setStatus(KnowledgeConstants.DOC_STATUS_FAILED);
        documentRepository.save(entity);
        knowledgebaseService.refreshKnowledgeBaseStats(knowledgeBaseId);
        log.info("文档删除完成, knowledgeBaseId={}, documentId={}", knowledgeBaseId, documentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeIndexTaskResponse reindexDocument(Long knowledgeBaseId, Long documentId) {
        KnowledgebaseEntity knowledgebase = getKnowledgeBase(knowledgeBaseId);
        KnowledgeDocumentEntity document = getDocumentEntity(knowledgeBaseId, documentId);

        taskRepository.findFirstByDocumentIdAndStatusInOrderByCreatedAtDesc(
                documentId, List.of(KnowledgeConstants.TASK_STATUS_PENDING, KnowledgeConstants.TASK_STATUS_PROCESSING))
                .ifPresent(existing -> {
                    throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "当前文档已有执行中的索引任务");
                });

        document.setStatus(KnowledgeConstants.DOC_STATUS_UPLOADED);
        document.setErrorMessage(null);
        documentRepository.save(document);

        KnowledgeIndexTaskEntity task = createIndexTask(knowledgebase, document, "reindex");
        publishTaskAfterCommit(task.getId());
        return knowledgebaseConverter.toKnowledgeIndexTaskResponse(task);
    }

    @Override
    public List<KnowledgeChunkResponse> listDocumentChunks(Long knowledgeBaseId, Long documentId) {
        getDocumentEntity(knowledgeBaseId, documentId);
        return chunkRepository.findByDocumentIdOrderByChunkNoAsc(documentId).stream()
                .map(knowledgebaseConverter::toKnowledgeChunkResponse)
                .toList();
    }

    @Override
    public List<KnowledgeIndexTaskResponse> listTasks(Long knowledgeBaseId) {
        getKnowledgeBase(knowledgeBaseId);
        return taskRepository.findByKnowledgeBaseIdOrderByCreatedAtDesc(knowledgeBaseId).stream()
                .map(knowledgebaseConverter::toKnowledgeIndexTaskResponse)
                .toList();
    }

    @Override
    public KnowledgeIndexTaskResponse getTask(Long knowledgeBaseId, Long taskId) {
        getKnowledgeBase(knowledgeBaseId);
        KnowledgeIndexTaskEntity task = taskRepository.findByIdAndKnowledgeBaseId(taskId, knowledgeBaseId)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "任务不存在"));
        return knowledgebaseConverter.toKnowledgeIndexTaskResponse(task);
    }

    private KnowledgebaseEntity getKnowledgeBase(Long knowledgeBaseId) {
        return knowledgebaseRepository.findByIdAndDeletedFalse(knowledgeBaseId)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "知识库不存在"));
    }

    private KnowledgeDocumentEntity getDocumentEntity(Long knowledgeBaseId, Long documentId) {
        return documentRepository.findByIdAndKnowledgeBaseIdAndDeletedFalse(documentId, knowledgeBaseId)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "文档不存在"));
    }

    private KnowledgeIndexTaskEntity createIndexTask(KnowledgebaseEntity knowledgebase,
                                                     KnowledgeDocumentEntity document,
                                                     String scene) {
        String idempotencyKey = knowledgebase.getId() + ":" + document.getId() + ":" + scene + ":" + UUID.randomUUID();
        KnowledgeIndexTaskEntity task = new KnowledgeIndexTaskEntity();
        task.setKnowledgeBaseId(knowledgebase.getId());
        task.setDocumentId(document.getId());
        task.setTaskType(KnowledgeConstants.TASK_TYPE_INDEX);
        task.setStatus(KnowledgeConstants.TASK_STATUS_PENDING);
        task.setRetryCount(0);
        task.setMaxRetryCount(3);
        task.setIdempotencyKey(idempotencyKey);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    private void publishTaskAfterCommit(Long taskId) {
        // 事务提交后再投递，避免消费者读到未提交任务
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            taskStreamProducer.sendTask(taskId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                taskStreamProducer.sendTask(taskId);
            }
        });
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), "未登录用户无法操作");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), "登录用户不存在"));
        return user.getId();
    }
}

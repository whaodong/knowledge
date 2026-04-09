package knowledge.aiapp.modules.knowledgebase.service;

import java.util.List;
import knowledge.aiapp.common.enums.ResultCodeEnum;
import knowledge.aiapp.common.exception.BusinessException;
import knowledge.aiapp.modules.knowledgebase.constant.KnowledgeConstants;
import knowledge.aiapp.modules.knowledgebase.converter.KnowledgebaseConverter;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgebaseEntity;
import knowledge.aiapp.modules.knowledgebase.dto.request.CreateKnowledgeBaseRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeBaseResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeBaseStatsResponse;
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

/**
 * 知识库管理服务实现。
 */
@Slf4j
@Service
public class KnowledgebaseServiceImpl implements KnowledgebaseService {

    private final KnowledgebaseRepository knowledgebaseRepository;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final KnowledgeIndexTaskRepository knowledgeIndexTaskRepository;
    private final KnowledgebaseConverter knowledgebaseConverter;
    private final UserRepository userRepository;

    public KnowledgebaseServiceImpl(KnowledgebaseRepository knowledgebaseRepository,
                                    KnowledgeDocumentRepository knowledgeDocumentRepository,
                                    KnowledgeChunkRepository knowledgeChunkRepository,
                                    KnowledgeIndexTaskRepository knowledgeIndexTaskRepository,
                                    KnowledgebaseConverter knowledgebaseConverter,
                                    UserRepository userRepository) {
        this.knowledgebaseRepository = knowledgebaseRepository;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
        this.knowledgeChunkRepository = knowledgeChunkRepository;
        this.knowledgeIndexTaskRepository = knowledgeIndexTaskRepository;
        this.knowledgebaseConverter = knowledgebaseConverter;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeBaseResponse createKnowledgeBase(CreateKnowledgeBaseRequest request) {
        KnowledgebaseEntity entity = new KnowledgebaseEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(KnowledgeConstants.KB_STATUS_ACTIVE);
        entity.setOwnerUserId(getCurrentUserId());
        entity.setDocumentCount(0);
        entity.setChunkCount(0);
        entity.setCompletedDocCount(0);
        entity.setFailedDocCount(0);
        entity.setDeleted(false);
        KnowledgebaseEntity saved = knowledgebaseRepository.save(entity);
        log.info("知识库创建成功, knowledgeBaseId={}, name={}", saved.getId(), saved.getName());
        return knowledgebaseConverter.toKnowledgeBaseResponse(saved);
    }

    @Override
    public List<KnowledgeBaseResponse> listKnowledgeBases() {
        return knowledgebaseRepository.findByDeletedFalseOrderByCreatedAtDesc().stream()
                .map(knowledgebaseConverter::toKnowledgeBaseResponse)
                .toList();
    }

    @Override
    public KnowledgeBaseResponse getKnowledgeBase(Long knowledgeBaseId) {
        return knowledgebaseConverter.toKnowledgeBaseResponse(getKnowledgeBaseEntity(knowledgeBaseId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledgeBase(Long knowledgeBaseId) {
        KnowledgebaseEntity entity = getKnowledgeBaseEntity(knowledgeBaseId);
        entity.setDeleted(true);
        entity.setStatus(KnowledgeConstants.KB_STATUS_DISABLED);
        knowledgebaseRepository.save(entity);
        log.info("知识库删除完成, knowledgeBaseId={}", knowledgeBaseId);
    }

    @Override
    public KnowledgeBaseStatsResponse getKnowledgeBaseStats(Long knowledgeBaseId) {
        KnowledgebaseEntity entity = getKnowledgeBaseEntity(knowledgeBaseId);
        long documentCount = knowledgeDocumentRepository.countByKnowledgeBaseIdAndDeletedFalse(knowledgeBaseId);
        long completedDocCount = knowledgeDocumentRepository.countByKnowledgeBaseIdAndStatusAndDeletedFalse(
                knowledgeBaseId, KnowledgeConstants.DOC_STATUS_COMPLETED);
        long failedDocCount = knowledgeDocumentRepository.countByKnowledgeBaseIdAndStatusAndDeletedFalse(
                knowledgeBaseId, KnowledgeConstants.DOC_STATUS_FAILED);
        long processingTaskCount = knowledgeIndexTaskRepository.countByKnowledgeBaseIdAndStatus(
                knowledgeBaseId, KnowledgeConstants.TASK_STATUS_PROCESSING)
                + knowledgeIndexTaskRepository.countByKnowledgeBaseIdAndStatus(
                knowledgeBaseId, KnowledgeConstants.TASK_STATUS_PENDING);
        long chunkCount = knowledgeChunkRepository.countByKnowledgeBaseId(knowledgeBaseId);

        return KnowledgeBaseStatsResponse.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .documentCount(documentCount)
                .chunkCount(chunkCount)
                .completedDocCount(completedDocCount)
                .failedDocCount(failedDocCount)
                .processingTaskCount(processingTaskCount)
                .lastIndexedAt(entity.getLastIndexedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshKnowledgeBaseStats(Long knowledgeBaseId) {
        KnowledgebaseEntity entity = getKnowledgeBaseEntity(knowledgeBaseId);
        KnowledgeBaseStatsResponse stats = getKnowledgeBaseStats(knowledgeBaseId);
        entity.setDocumentCount(stats.getDocumentCount().intValue());
        entity.setChunkCount(stats.getChunkCount().intValue());
        entity.setCompletedDocCount(stats.getCompletedDocCount().intValue());
        entity.setFailedDocCount(stats.getFailedDocCount().intValue());
        knowledgebaseRepository.save(entity);
    }

    private KnowledgebaseEntity getKnowledgeBaseEntity(Long knowledgeBaseId) {
        return knowledgebaseRepository.findByIdAndDeletedFalse(knowledgeBaseId)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "知识库不存在"));
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

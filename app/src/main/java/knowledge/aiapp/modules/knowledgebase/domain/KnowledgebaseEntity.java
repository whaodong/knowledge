package knowledge.aiapp.modules.knowledgebase.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knowledge.aiapp.infrastructure.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 知识库实体。
 */
@Getter
@Setter
@Entity
@Table(name = "knowledge_base")
public class KnowledgebaseEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Column(name = "document_count", nullable = false)
    private Integer documentCount;

    @Column(name = "chunk_count", nullable = false)
    private Integer chunkCount;

    @Column(name = "completed_doc_count", nullable = false)
    private Integer completedDocCount;

    @Column(name = "failed_doc_count", nullable = false)
    private Integer failedDocCount;

    @Column(name = "last_indexed_at")
    private java.time.LocalDateTime lastIndexedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;
}

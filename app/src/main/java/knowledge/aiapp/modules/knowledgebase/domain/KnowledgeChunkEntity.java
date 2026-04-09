package knowledge.aiapp.modules.knowledgebase.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knowledge.aiapp.infrastructure.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 知识分块实体。
 */
@Getter
@Setter
@Entity
@Table(name = "knowledge_chunk")
public class KnowledgeChunkEntity extends BaseEntity {

    @Column(name = "knowledge_base_id", nullable = false)
    private Long knowledgeBaseId;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "chunk_no", nullable = false)
    private Integer chunkNo;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_preview", length = 500)
    private String contentPreview;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "char_count")
    private Integer charCount;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata_json", columnDefinition = "jsonb")
    private String metadataJson;

    @Column(name = "vector_ref_id", length = 100)
    private String vectorRefId;
}

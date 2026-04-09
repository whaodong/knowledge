package knowledge.aiapp.modules.knowledgebase.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knowledge.aiapp.infrastructure.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 知识文档实体。
 */
@Getter
@Setter
@Entity
@Table(name = "knowledge_document")
public class KnowledgeDocumentEntity extends BaseEntity {

    @Column(name = "knowledge_base_id", nullable = false)
    private Long knowledgeBaseId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_ext", nullable = false, length = 20)
    private String fileExt;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "parser_type", length = 30)
    private String parserType;

    @Column(name = "char_count")
    private Integer charCount;

    @Column(name = "chunk_count", nullable = false)
    private Integer chunkCount;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;
}

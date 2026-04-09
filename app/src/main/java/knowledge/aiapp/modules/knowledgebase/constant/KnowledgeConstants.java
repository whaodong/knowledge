package knowledge.aiapp.modules.knowledgebase.constant;

/**
 * 知识库模块常量。
 */
public final class KnowledgeConstants {

    public static final String KB_STATUS_ACTIVE = "ACTIVE";
    public static final String KB_STATUS_DISABLED = "DISABLED";

    public static final String DOC_STATUS_UPLOADED = "UPLOADED";
    public static final String DOC_STATUS_PARSING = "PARSING";
    public static final String DOC_STATUS_PARSED = "PARSED";
    public static final String DOC_STATUS_CHUNKED = "CHUNKED";
    public static final String DOC_STATUS_VECTORIZING = "VECTORIZING";
    public static final String DOC_STATUS_COMPLETED = "COMPLETED";
    public static final String DOC_STATUS_FAILED = "FAILED";

    public static final String TASK_TYPE_INDEX = "INDEX";
    public static final String TASK_STATUS_PENDING = "PENDING";
    public static final String TASK_STATUS_PROCESSING = "PROCESSING";
    public static final String TASK_STATUS_SUCCESS = "SUCCESS";
    public static final String TASK_STATUS_FAILED = "FAILED";

    public static final String MESSAGE_ROLE_USER = "USER";
    public static final String MESSAGE_ROLE_ASSISTANT = "ASSISTANT";
    public static final String MESSAGE_ROLE_SYSTEM = "SYSTEM";

    private KnowledgeConstants() {
    }
}

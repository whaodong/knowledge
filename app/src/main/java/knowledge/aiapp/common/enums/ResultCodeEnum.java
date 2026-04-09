package knowledge.aiapp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一返回码枚举。
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "系统内部错误");

    private final Integer code;
    private final String message;
}

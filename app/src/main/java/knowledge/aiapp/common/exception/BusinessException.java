package knowledge.aiapp.common.exception;

import knowledge.aiapp.common.enums.ResultCodeEnum;
import lombok.Getter;

/**
 * 统一业务异常，用于表达可预期的业务失败。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCodeEnum.BAD_REQUEST.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}

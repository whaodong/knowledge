package knowledge.aiapp.common.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import knowledge.aiapp.common.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 通用接口返回体。
 *
 * @param <T> 业务数据类型
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> Result<T> success() {
        return success(ResultCodeEnum.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> success(T data) {
        return success(ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), message, data, LocalDateTime.now());
    }

    public static <T> Result<T> failure(Integer code, String message) {
        return new Result<>(code, message, null, LocalDateTime.now());
    }

    public static <T> Result<T> failure(ResultCodeEnum resultCodeEnum) {
        return failure(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }
}

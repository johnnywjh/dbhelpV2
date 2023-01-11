package kim.sesame.common.exception;

import kim.sesame.common.response.IErrorCode;

/**
 * 异常接口
 */
public interface IException {

    IErrorCode getErrorCodeEnum();

    String getMessage();
}

package com.sesame.common.exception;

import com.sesame.common.response.IErrorCode;

/**
 * 异常接口
 */
public interface IException {

    IErrorCode getErrorCodeEnum();

    String getMessage();
}

package com.sichengbo.su.common;

import java.io.Serializable;

/**
 * 通用响应信息
 */
public class CommonResult implements Serializable {

    private static final long serialVersionUID = 4359709211352400087L;

    private long code;

    private String message;

    private Object data;

    public CommonResult() {
    }

    public CommonResult(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public void setCode(final long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public void setResultCode(ResultCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public CommonResult(long code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static CommonResult success() {
        CommonResult result = new CommonResult();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    public static CommonResult success(ResultCode resultCode) {
        CommonResult result = new CommonResult();
        result.setResultCode(resultCode);
        return result;
    }

    public static CommonResult success(Object data) {
        CommonResult result = new CommonResult();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static CommonResult success(Object data, ResultCode resultCode) {
        CommonResult result = new CommonResult();
        result.setData(data);
        result.setResultCode(resultCode);
        return result;
    }

    public static CommonResult failure(ResultCode resultCode) {
        CommonResult result = new CommonResult();
        result.setResultCode(resultCode);
        return result;
    }

    public static CommonResult failure(ResultCode resultCode, Object data) {
        CommonResult result = new CommonResult();
        result.setResultCode(resultCode);
        result.setData(data);
        return result;
    }

    public static CommonResult failure(String message) {
        CommonResult result = new CommonResult();
        result.setMessage(message);
        return result;
    }
}

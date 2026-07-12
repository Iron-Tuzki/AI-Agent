package com.example.agent.api.common;

/**
 * API 统一响应码枚举，用于集中维护接口成功和失败场景的业务编码。
 */
public enum ResponseCode {

    /** 请求处理成功。 */
    SUCCESS("SUCCESS"),

    /** 请求参数校验失败。 */
    BAD_REQUEST("BAD_REQUEST"),

    /** 业务规则校验失败。 */
    BUSINESS_ERROR("BUSINESS_ERROR"),

    /** 系统内部异常。 */
    SYSTEM_ERROR("SYSTEM_ERROR");

    private final String code;

    ResponseCode(String code) {
        this.code = code;
    }

    /**
     * 获取接口返回的字符串编码。
     *
     * @return 响应码字符串
     */
    public String getCode() {
        return code;
    }
}

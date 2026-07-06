package com.example.agent.api.common;

/**
 * 统一 API 响应体，用于规范 HTTP 接口的成功标识、业务状态码、提示信息和响应数据。
 *
 * @param success 请求是否处理成功
 * @param code 业务状态码
 * @param message 响应提示信息
 * @param data 响应数据
 * @param <T> 响应数据类型
 */
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
) {

    /**
     * 构造成功响应。
     *
     * @param data 响应数据
     * @param <T> 响应数据类型
     * @return 统一成功响应
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "SUCCESS", "请求成功", data);
    }

    /**
     * 构造失败响应。
     *
     * @param code 业务状态码
     * @param message 错误提示信息
     * @param <T> 响应数据类型
     * @return 统一失败响应
     */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}

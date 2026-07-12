package com.example.agent.api.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * 统一响应体测试，验证响应码枚举能够正确转换为接口返回值。
 */
class ApiResponseTest {

    @Test
    void shouldBuildFailedResponseWithResponseCodeEnum() {
        ApiResponse<Void> response = ApiResponse.fail(ResponseCode.BUSINESS_ERROR, "业务处理失败");

        assertFalse(response.success());
        assertEquals("BUSINESS_ERROR", response.code());
        assertEquals("业务处理失败", response.message());
    }
}

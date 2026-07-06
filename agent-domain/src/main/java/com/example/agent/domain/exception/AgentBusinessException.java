package com.example.agent.domain.exception;

/**
 * Agent 业务异常，用于表达参数合法但业务状态不满足要求的场景。
 */
public class AgentBusinessException extends RuntimeException {

    public AgentBusinessException(String message) {
        super(message);
    }

    public AgentBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

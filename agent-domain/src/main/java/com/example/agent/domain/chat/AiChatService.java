package com.example.agent.domain.chat;

import com.example.agent.domain.provider.AiProvider;
import reactor.core.publisher.Flux;

/**
 * AI 对话服务接口，用于屏蔽 Spring AI、LangChain4j 等不同框架的调用差异。
 */
public interface AiChatService {

    /**
     * 判断当前实现是否支持指定 AI 框架提供方。
     *
     * @param provider AI 框架提供方
     * @return 如果当前实现支持该提供方则返回 true
     */
    boolean supports(AiProvider provider);

    /**
     * 执行普通 AI 对话。
     *
     * @param request AI 对话请求，包含提供方、会话编号和用户消息
     * @return AI 对话结果
     */
    AiChatResult chat(AiChatRequest request);

    /**
     * 执行流式 AI 对话。
     *
     * @param request AI 对话请求，包含提供方、会话编号和用户消息
     * @return AI 回复内容片段流
     */
    default Flux<String> stream(AiChatRequest request) {
        return Flux.error(new UnsupportedOperationException("当前 AI 对话实现暂不支持流式输出"));
    }
}

package com.example.agent.api.chat;

import com.example.agent.api.common.ApiResponse;
import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.chat.AiChatService;
import com.example.agent.domain.exception.AgentBusinessException;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 对话接口，负责接收 HTTP 对话请求并根据提供方路由到对应的 AI 对话服务实现。
 */
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final List<AiChatService> chatServices;

    public AiChatController(List<AiChatService> chatServices) {
        this.chatServices = chatServices;
    }

    /**
     * 执行普通 AI 对话。
     *
     * @param request AI 对话请求
     * @return AI 对话结果
     */
    @PostMapping("/chat")
    public ApiResponse<AiChatResult> chat(@Valid @RequestBody AiChatRequest request) {
        return ApiResponse.ok(resolveService(request).chat(request));
    }

    /**
     * 执行流式 AI 对话。
     *
     * @param request AI 对话请求
     * @return AI 回复内容片段流
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@Valid @RequestBody AiChatRequest request) {
        return resolveService(request).stream(request);
    }

    private AiChatService resolveService(AiChatRequest request) {
        return chatServices.stream()
                .filter(service -> service.supports(request.provider()))
                .findFirst()
                .orElseThrow(() -> new AgentBusinessException("不支持的 AI 提供方：" + request.provider()));
    }
}

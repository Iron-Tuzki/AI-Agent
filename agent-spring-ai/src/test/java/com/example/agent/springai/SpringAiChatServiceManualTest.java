package com.example.agent.springai;

import com.example.agent.domain.chat.AiChatRequest;
import com.example.agent.domain.chat.AiChatResult;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Spring AI 千问真实调用手动测试，用于在控制台查看模型回复。
 */
@EnabledIfEnvironmentVariable(named = "RUN_QWEN_MANUAL_TEST", matches = "true")
@SpringBootTest(
        classes = SpringAiChatServiceManualTest.ManualTestApplication.class,
        properties = {
                "spring.ai.openai.api-key=${DASHSCOPE_API_KEY}",
                "spring.ai.openai.base-url=${DASHSCOPE_BASE_URL:https://dashscope.aliyuncs.com/compatible-mode}",
                "spring.ai.openai.chat.options.model=${DASHSCOPE_CHAT_MODEL:qwen-plus}"
        }
)
class SpringAiChatServiceManualTest {

    @Autowired
    private SpringAiChatService springAiChatService;

    @Test
    void shouldPrintRealQwenReply() {
        AiChatRequest request = new AiChatRequest(
                AiProvider.SPRING_AI,
                "manual-console-test",
                "Java Agent 工程师需要什么技能"
        );

        AiChatResult result = springAiChatService.chat(request);

        System.out.println();
        System.out.println("========== 千问回复 ==========");
        System.out.println(result.content());
        System.out.println("============================");
    }

    @SpringBootApplication
    @Import({SpringAiChatService.class, SpringAiHttpClientConfiguration.class})
    static class ManualTestApplication {
    }
}

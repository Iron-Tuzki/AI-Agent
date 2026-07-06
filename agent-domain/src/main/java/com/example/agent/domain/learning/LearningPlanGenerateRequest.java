package com.example.agent.domain.learning;

import com.example.agent.domain.provider.AiProvider;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 学习计划生成请求，描述学习主题、学习周期、能力等级和 AI 框架提供方。
 *
 * @param provider AI 框架提供方，用于选择学习计划生成的具体实现
 * @param topic 学习主题，例如 JVM、Redis、Spring AI
 * @param days 学习周期天数，范围为 1 到 30 天
 * @param level 学员当前能力等级，例如 BEGINNER、INTERMEDIATE、ADVANCED
 */
public record LearningPlanGenerateRequest(
        @NotNull AiProvider provider,
        @NotBlank String topic,
        @Min(1) @Max(30) int days,
        @NotBlank String level
) {
}

package com.example.agent.domain.learning;

import com.example.agent.domain.provider.AiProvider;

/**
 * 学习计划服务接口，用于根据学习目标生成结构化学习计划。
 */
public interface LearningPlanService {

    /**
     * 判断当前实现是否支持指定 AI 框架提供方。
     *
     * @param provider AI 框架提供方
     * @return 如果当前实现支持该提供方则返回 true
     */
    boolean supports(AiProvider provider);

    /**
     * 生成结构化学习计划。
     *
     * @param request 学习计划生成请求，包含主题、天数、等级和提供方
     * @return 结构化学习计划
     */
    LearningPlan generate(LearningPlanGenerateRequest request);
}

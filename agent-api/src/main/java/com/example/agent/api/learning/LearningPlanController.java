package com.example.agent.api.learning;

import com.example.agent.api.common.ApiResponse;
import com.example.agent.domain.exception.AgentBusinessException;
import com.example.agent.domain.learning.LearningPlan;
import com.example.agent.domain.learning.LearningPlanGenerateRequest;
import com.example.agent.domain.learning.LearningPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学习计划接口，负责接收 HTTP 请求并根据提供方生成结构化学习计划。
 */
@RestController
@RequestMapping("/api/learning/plans")
public class LearningPlanController {

    private final List<LearningPlanService> learningPlanServices;

    public LearningPlanController(List<LearningPlanService> learningPlanServices) {
        this.learningPlanServices = learningPlanServices;
    }

    /**
     * 生成结构化学习计划。
     *
     * @param request 学习计划生成请求
     * @return 结构化学习计划
     */
    @PostMapping("/generate")
    public ApiResponse<LearningPlan> generate(@Valid @RequestBody LearningPlanGenerateRequest request) {
        LearningPlanService service = learningPlanServices.stream()
                .filter(item -> item.supports(request.provider()))
                .findFirst()
                .orElseThrow(() -> new AgentBusinessException("不支持的 AI 提供方：" + request.provider()));
        return ApiResponse.ok(service.generate(request));
    }
}

package com.example.agent.api.learning;

import com.example.agent.api.common.GlobalExceptionHandler;
import com.example.agent.domain.learning.LearningPlan;
import com.example.agent.domain.learning.LearningPlanService;
import com.example.agent.domain.provider.AiProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LearningPlanController.class)
@ContextConfiguration(classes = {LearningPlanController.class, GlobalExceptionHandler.class})
class LearningPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LearningPlanService learningPlanService;

    @Test
    void shouldReturnLearningPlanWhenProviderIsSupported() throws Exception {
        LearningPlan plan = new LearningPlan(
                "JVM GC",
                7,
                "ADVANCED",
                List.of(new LearningPlan.LearningStage("GC 基础", "第 1-2 天", List.of("理解可达性分析"))),
                List.of(new LearningPlan.PracticeTask("分析 GC 日志", "使用工具分析一次 GC 日志", "能说明停顿原因")),
                List.of(new LearningPlan.InterviewQuestion("什么是 G1？", List.of("分区收集", "控制停顿")))
        );
        when(learningPlanService.supports(AiProvider.SPRING_AI)).thenReturn(true);
        when(learningPlanService.generate(any())).thenReturn(plan);

        mockMvc.perform(post("/api/learning/plans/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "SPRING_AI",
                                  "topic": "JVM GC",
                                  "days": 7,
                                  "level": "ADVANCED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.topic").value("JVM GC"))
                .andExpect(jsonPath("$.data.days").value(7))
                .andExpect(jsonPath("$.data.stages[0].name").value("GC 基础"));
    }

    @Test
    void shouldReturnBadRequestWhenDaysExceedsLimit() throws Exception {
        mockMvc.perform(post("/api/learning/plans/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider": "SPRING_AI",
                                  "topic": "JVM GC",
                                  "days": 31,
                                  "level": "ADVANCED"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }
}

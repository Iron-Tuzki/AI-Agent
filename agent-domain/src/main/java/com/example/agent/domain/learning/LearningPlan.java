package com.example.agent.domain.learning;

import java.util.List;

/**
 * 结构化学习计划，描述某个主题的阶段安排、练习任务和面试题。
 *
 * @param topic 学习主题
 * @param days 学习周期天数
 * @param level 学员能力等级
 * @param stages 阶段化学习安排
 * @param practiceTasks 项目化练习任务列表
 * @param interviewQuestions 高频面试题列表
 */
public record LearningPlan(
        String topic,
        int days,
        String level,
        List<LearningStage> stages,
        List<PracticeTask> practiceTasks,
        List<InterviewQuestion> interviewQuestions
) {

    /**
     * 学习阶段，表示某一阶段的目标、天数范围和关键知识点。
     *
     * @param name 阶段名称
     * @param dayRange 阶段覆盖的天数范围
     * @param goals 当前阶段需要达成的学习目标
     */
    public record LearningStage(String name, String dayRange, List<String> goals) {
    }

    /**
     * 练习任务，表示用于巩固知识点的项目化实践。
     *
     * @param title 练习任务标题
     * @param description 练习任务描述
     * @param expectedOutcome 完成任务后应达到的产出或效果
     */
    public record PracticeTask(String title, String description, String expectedOutcome) {
    }

    /**
     * 面试题，表示学习主题对应的高频问题和参考回答要点。
     *
     * @param question 面试问题
     * @param answerPoints 参考回答要点
     */
    public record InterviewQuestion(String question, List<String> answerPoints) {
    }
}

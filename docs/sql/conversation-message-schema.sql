CREATE DATABASE AI_AGENT;

CREATE TABLE agent_conversation (
    id VARCHAR(64) PRIMARY KEY COMMENT '会话编号',
    title VARCHAR(128) NOT NULL COMMENT '会话标题',
    provider VARCHAR(32) NOT NULL COMMENT 'AI 框架提供方',
    status VARCHAR(32) NOT NULL COMMENT '会话状态',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_agent_conversation_updated_at (updated_at)
) COMMENT = 'AI 对话会话表';

CREATE TABLE agent_message (
    id VARCHAR(64) PRIMARY KEY COMMENT '消息编号',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话编号',
    role VARCHAR(32) NOT NULL COMMENT '消息角色',
    content TEXT NOT NULL COMMENT '消息内容',
    status VARCHAR(32) NOT NULL COMMENT '消息状态',
    provider VARCHAR(32) NOT NULL COMMENT 'AI 框架提供方',
    prompt_tokens INT NULL COMMENT '输入 Token 数',
    completion_tokens INT NULL COMMENT '输出 Token 数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_agent_message_conversation_created_at (conversation_id, created_at)
) COMMENT = 'AI 对话消息表';

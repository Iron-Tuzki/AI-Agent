export type AiProvider = 'SPRING_AI' | 'LANGCHAIN4J'

export type ConversationStatus = 'ACTIVE' | 'ARCHIVED'
export type ConversationRole = 'USER' | 'ASSISTANT' | 'SYSTEM'
export type MessageStatus = 'SUCCESS' | 'FAILED' | 'STREAMING'

export interface Conversation {
  id: string
  title: string
  provider: AiProvider
  status: ConversationStatus
  createdAt: string
  updatedAt: string
}

export interface ConversationMessage {
  id: string
  conversationId: string
  role: ConversationRole
  content: string
  status: MessageStatus
  provider: AiProvider
  promptTokens: number | null
  completionTokens: number | null
  createdAt: string
  updatedAt: string
}

export interface ChatRequest {
  provider: AiProvider
  conversationId: string | null
  message: string
}

export type SseEvent =
  | { type: 'conversation'; conversationId: string }
  | { type: 'content'; content: string }

export interface ApiResponse<T> {
  success: boolean
  code: string
  message: string
  data: T
}

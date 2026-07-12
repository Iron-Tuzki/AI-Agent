import type { ApiResponse, Conversation, ConversationMessage } from '../types/chat'

async function readResponse<T>(response: Response): Promise<T> {
  const payload = await response.json() as ApiResponse<T>
  if (!response.ok || !payload.success) {
    throw new Error(payload.message || '请求失败')
  }
  return payload.data
}

export async function listConversations(signal?: AbortSignal): Promise<Conversation[]> {
  return readResponse(await fetch('/api/conversations', { signal }))
}

export async function listMessages(
  conversationId: string,
  signal?: AbortSignal,
): Promise<ConversationMessage[]> {
  return readResponse(await fetch(`/api/conversations/${conversationId}/messages`, { signal }))
}

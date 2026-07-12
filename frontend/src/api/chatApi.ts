import { parseSseEvent, splitSseBuffer } from './sse'
import type { ChatRequest, SseEvent } from '../types/chat'

export async function streamChat(
  request: ChatRequest,
  signal: AbortSignal,
  onEvent: (event: SseEvent) => void,
): Promise<void> {
  const response = await fetch('/api/ai/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'text/event-stream',
    },
    body: JSON.stringify(request),
    signal,
  })

  if (!response.ok || !response.body) {
    let message = 'AI 回复失败'
    try {
      const payload = await response.json() as { message?: string }
      message = payload.message || message
    } catch {
      // 非 JSON 错误响应使用默认消息。
    }
    throw new Error(message)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  let buffer = ''

  try {
    while (true) {
      const { done, value } = await reader.read()
      buffer += decoder.decode(value, { stream: !done })
      const split = splitSseBuffer(buffer)
      buffer = split.remainder
      split.events.forEach((raw) => {
        const event = parseSseEvent(raw)
        if (event) onEvent(event)
      })
      if (done) break
    }

    if (buffer.trim()) {
      const event = parseSseEvent(buffer)
      if (event) onEvent(event)
    }
  } finally {
    reader.releaseLock()
  }
}

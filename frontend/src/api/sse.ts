import type { SseEvent } from '../types/chat'

export function parseSseEvent(raw: string): SseEvent | null {
  const eventName = raw.match(/^event:\s*(.+)$/m)?.[1]?.trim()
  const data = raw.match(/^data:\s?(.*)$/m)?.[1] ?? ''

  if (eventName === 'conversation') {
    const payload = JSON.parse(data) as { conversationId?: string }
    if (!payload.conversationId) {
      return null
    }
    return { type: 'conversation', conversationId: payload.conversationId }
  }

  if (data) {
    return { type: 'content', content: data }
  }

  return null
}

export function splitSseBuffer(buffer: string): { events: string[]; remainder: string } {
  const chunks = buffer.split('\n\n')
  return {
    events: chunks.slice(0, -1),
    remainder: chunks.at(-1) ?? '',
  }
}

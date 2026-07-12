import { describe, expect, it } from 'vitest'
import { parseSseEvent } from './sse'

describe('parseSseEvent', () => {
  it('parses a conversation event and JSON payload', () => {
    expect(parseSseEvent('event: conversation\ndata: {"conversationId":"c001"}\n\n'))
      .toEqual({ type: 'conversation', conversationId: 'c001' })
  })

  it('parses ordinary data events as content', () => {
    expect(parseSseEvent('data: Java Agent\n\n'))
      .toEqual({ type: 'content', content: 'Java Agent' })
  })
})

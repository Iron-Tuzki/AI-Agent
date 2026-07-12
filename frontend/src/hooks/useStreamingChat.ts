import { useCallback, useRef, useState } from 'react'
import { streamChat } from '../api/chatApi'
import type { ChatRequest, SseEvent } from '../types/chat'

export function useStreamingChat() {
  const controllerRef = useRef<AbortController | null>(null)
  const [isStreaming, setIsStreaming] = useState(false)

  const send = useCallback(async (
    request: ChatRequest,
    onEvent: (event: SseEvent) => void,
  ) => {
    controllerRef.current?.abort()
    const controller = new AbortController()
    controllerRef.current = controller
    setIsStreaming(true)
    try {
      await streamChat(request, controller.signal, onEvent)
    } finally {
      if (controllerRef.current === controller) {
        controllerRef.current = null
        setIsStreaming(false)
      }
    }
  }, [])

  const stop = useCallback(() => {
    controllerRef.current?.abort()
  }, [])

  return { isStreaming, send, stop }
}

import { useEffect, useMemo, useState } from 'react'
import { listConversations, listMessages } from './api/conversationApi'
import { useStreamingChat } from './hooks/useStreamingChat'
import type { AiProvider, Conversation, ConversationMessage, SseEvent } from './types/chat'
import { ChatHeader } from './components/ChatHeader'
import { ConversationSidebar } from './components/ConversationSidebar'
import { MessageComposer } from './components/MessageComposer'
import { MessageList } from './components/MessageList'
import './styles.css'

function temporaryMessage(role: ConversationMessage['role'], content: string, status: ConversationMessage['status']): ConversationMessage {
  const now = new Date().toISOString()
  return { id: `temp-${crypto.randomUUID()}`, conversationId: '', role, content, status, provider: 'SPRING_AI', promptTokens: null, completionTokens: null, createdAt: now, updatedAt: now }
}

export default function App() {
  const [conversations, setConversations] = useState<Conversation[]>([])
  const [activeId, setActiveId] = useState<string | null>(null)
  const [messages, setMessages] = useState<ConversationMessage[]>([])
  const [provider, setProvider] = useState<AiProvider>('SPRING_AI')
  const [draft, setDraft] = useState('')
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { isStreaming, send, stop } = useStreamingChat()

  const activeConversation = useMemo(() => conversations.find((item) => item.id === activeId), [conversations, activeId])

  const refreshConversations = async () => {
    try { setConversations(await listConversations()) } catch (cause) { setError(cause instanceof Error ? cause.message : '会话列表加载失败') }
  }

  useEffect(() => { void refreshConversations() }, [])

  const selectConversation = async (id: string) => {
    setError(null)
    setActiveId(id)
    try {
      const selected = conversations.find((item) => item.id === id)
      if (selected) setProvider(selected.provider)
      setMessages(await listMessages(id))
    } catch (cause) { setError(cause instanceof Error ? cause.message : '消息加载失败') }
  }

  const startNewConversation = () => { setActiveId(null); setMessages([]); setDraft(''); setError(null) }

  const handleEvent = (event: SseEvent, assistantId: string) => {
    if (event.type === 'conversation') {
      setActiveId(event.conversationId)
      return
    }
    setMessages((current) => current.map((message) => message.id === assistantId ? { ...message, content: message.content + event.content } : message))
  }

  const sendMessage = async () => {
    const message = draft.trim()
    if (!message || isStreaming) return
    setError(null)
    setDraft('')
    const userMessage = temporaryMessage('USER', message, 'SUCCESS')
    const assistantMessage = temporaryMessage('ASSISTANT', '', 'STREAMING')
    setMessages((current) => [...current, userMessage, assistantMessage])
    try {
      await send({ provider, conversationId: activeId, message }, (event) => handleEvent(event, assistantMessage.id))
      setMessages((current) => current.map((item) => item.id === assistantMessage.id ? { ...item, status: 'SUCCESS' } : item))
      await refreshConversations()
    } catch (cause) {
      if ((cause as Error).name === 'AbortError') {
        setMessages((current) => current.map((item) => item.id === assistantMessage.id ? { ...item, status: 'FAILED' } : item))
        return
      }
      setError(cause instanceof Error ? cause.message : 'AI 回复失败')
      setMessages((current) => current.map((item) => item.id === assistantMessage.id ? { ...item, status: 'FAILED' } : item))
    }
  }

  return (
    <div className="app-shell">
      <ConversationSidebar conversations={conversations} activeId={activeId} collapsed={sidebarCollapsed} onToggle={() => setSidebarCollapsed((value) => !value)} onSelect={(id) => void selectConversation(id)} onNew={startNewConversation} />
      <main className="chat-shell">
        <ChatHeader title={activeConversation?.title ?? '新建对话'} provider={provider} onOpenSidebar={() => setSidebarCollapsed(false)} />
        {error && <div className="error-banner">{error}<button onClick={() => setError(null)}>×</button></div>}
        <MessageList messages={messages} isLoading={isStreaming} />
        <MessageComposer provider={provider} value={draft} isStreaming={isStreaming} onProviderChange={setProvider} onChange={setDraft} onSend={() => void sendMessage()} onStop={stop} />
      </main>
    </div>
  )
}

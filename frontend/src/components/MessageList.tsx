import { useEffect, useRef } from 'react'
import type { ConversationMessage } from '../types/chat'
import { MessageBubble } from './MessageBubble'

interface Props { messages: ConversationMessage[]; isLoading: boolean }

export function MessageList({ messages, isLoading }: Props) {
  const endRef = useRef<HTMLDivElement>(null)
  useEffect(() => endRef.current?.scrollIntoView({ behavior: 'smooth' }), [messages, isLoading])

  if (messages.length === 0 && !isLoading) {
    return (
      <section className="empty-state">
        <div className="empty-state__orb">✦</div>
        <div className="chat-header__eyebrow">JAVA AGENT LEARNING ASSISTANT</div>
        <h2>今天想一起探索什么？</h2>
        <p>从 Chat、Prompt 到 Tool Calling，<br />把每一个 Agent 概念都落到真实代码里。</p>
        <div className="suggestion-row"><span>“解释一下 RAG 的核心流程”</span><span>“帮我规划 JVM 学习路线”</span></div>
      </section>
    )
  }

  return <section className="message-list">{messages.map((message) => <MessageBubble key={message.id} message={message} />)}<div ref={endRef} /></section>
}

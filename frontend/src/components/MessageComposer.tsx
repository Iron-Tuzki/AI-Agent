import { useRef } from 'react'
import type { AiProvider } from '../types/chat'

interface Props {
  provider: AiProvider
  value: string
  isStreaming: boolean
  onProviderChange: (provider: AiProvider) => void
  onChange: (value: string) => void
  onSend: () => void
  onStop: () => void
}

export function MessageComposer({ provider, value, isStreaming, onProviderChange, onChange, onSend, onStop }: Props) {
  const ref = useRef<HTMLTextAreaElement>(null)
  return (
    <footer className="composer-wrap">
      <div className="composer">
        <textarea
          ref={ref}
          value={value}
          disabled={isStreaming}
          onChange={(event) => onChange(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === 'Enter' && !event.shiftKey) {
              event.preventDefault()
              onSend()
            }
          }}
          placeholder="输入你想探索的 Java Agent 问题..."
          rows={1}
        />
        <div className="composer__bottom">
          <select value={provider} onChange={(event) => onProviderChange(event.target.value as AiProvider)} disabled={isStreaming}>
            <option value="SPRING_AI">Spring AI</option>
            <option value="LANGCHAIN4J">LangChain4j</option>
          </select>
          <span className="composer__hint">Shift + Enter 换行</span>
          {isStreaming ? <button className="send-button send-button--stop" onClick={onStop}>■ 停止</button> : <button className="send-button" onClick={onSend} disabled={!value.trim()}>发送 <span>↗</span></button>}
        </div>
      </div>
      <div className="composer__disclaimer">AI 生成的内容可能存在偏差，请结合源码和测试进行验证。</div>
    </footer>
  )
}

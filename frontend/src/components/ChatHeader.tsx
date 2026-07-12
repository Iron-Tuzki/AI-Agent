import type { AiProvider } from '../types/chat'

interface Props {
  title: string
  provider: AiProvider
  onOpenSidebar: () => void
}

export function ChatHeader({ title, provider, onOpenSidebar }: Props) {
  return (
    <header className="chat-header">
      <button className="icon-button mobile-menu" onClick={onOpenSidebar} aria-label="打开历史会话">☰</button>
      <div>
        <div className="chat-header__eyebrow">CONVERSATION</div>
        <h1>{title || '新建对话'}</h1>
      </div>
      <div className="provider-badge"><span /> {provider === 'SPRING_AI' ? 'Spring AI' : 'LangChain4j'}</div>
    </header>
  )
}

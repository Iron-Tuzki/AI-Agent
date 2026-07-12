import type { Conversation } from '../types/chat'

interface Props {
  conversations: Conversation[]
  activeId: string | null
  collapsed: boolean
  onToggle: () => void
  onSelect: (id: string) => void
  onNew: () => void
}

export function ConversationSidebar({ conversations, activeId, collapsed, onToggle, onSelect, onNew }: Props) {
  return (
    <aside className={`sidebar ${collapsed ? 'sidebar--collapsed' : ''}`}>
      <div className="sidebar__brand">
        <div className="brand-mark">⌘</div>
        {!collapsed && <div><strong>Java Agent</strong><span>Learning Lab</span></div>}
        <button className="icon-button sidebar__toggle" onClick={onToggle} aria-label="收起侧栏">
          {collapsed ? '›' : '‹'}
        </button>
      </div>

      {!collapsed && (
        <>
          <button className="new-chat" onClick={onNew}><span>＋</span> 新建对话</button>
          <div className="sidebar__label">最近对话</div>
          <nav className="conversation-list">
            {conversations.length === 0 && <div className="sidebar__empty">还没有历史对话</div>}
            {conversations.map((conversation) => (
              <button
                className={`conversation-item ${conversation.id === activeId ? 'conversation-item--active' : ''}`}
                key={conversation.id}
                onClick={() => onSelect(conversation.id)}
              >
                <span className="conversation-item__dot" />
                <span className="conversation-item__content">
                  <strong>{conversation.title || '未命名对话'}</strong>
                  <small>{conversation.provider === 'SPRING_AI' ? 'Spring AI' : 'LangChain4j'}</small>
                </span>
              </button>
            ))}
          </nav>
          <div className="sidebar__footer"><span className="status-dot" /> API 服务已连接</div>
        </>
      )}
    </aside>
  )
}

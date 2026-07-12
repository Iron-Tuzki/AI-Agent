import type { ConversationMessage } from '../types/chat'

function renderInline(text: string) {
  return text.split(/(`[^`]+`|\*\*[^*]+\*\*)/g).map((part, index) => {
    if (part.startsWith('`') && part.endsWith('`')) return <code key={index}>{part.slice(1, -1)}</code>
    if (part.startsWith('**') && part.endsWith('**')) return <strong key={index}>{part.slice(2, -2)}</strong>
    return <span key={index}>{part}</span>
  })
}

function renderMarkdown(content: string) {
  const sections = content.split('```')
  return sections.map((section, index) => {
    if (index % 2 === 1) {
      const lines = section.split('\n')
      const language = lines[0]?.trim()
      const code = language && /^[\w+#-]+$/.test(language) ? lines.slice(1).join('\n') : section
      return <pre key={index}><span className="code-language">{language && code !== section ? language : 'code'}</span><code>{code.trim()}</code></pre>
    }
    return section.split('\n').map((line, lineIndex) => {
      if (!line.trim()) return <div className="markdown-spacer" key={`${index}-${lineIndex}`} />
      if (line.startsWith('### ')) return <h4 key={`${index}-${lineIndex}`}>{renderInline(line.slice(4))}</h4>
      if (line.startsWith('## ')) return <h3 key={`${index}-${lineIndex}`}>{renderInline(line.slice(3))}</h3>
      if (line.startsWith('# ')) return <h2 key={`${index}-${lineIndex}`}>{renderInline(line.slice(2))}</h2>
      if (line.startsWith('- ')) return <div className="markdown-list" key={`${index}-${lineIndex}`}>• {renderInline(line.slice(2))}</div>
      if (line.startsWith('> ')) return <blockquote key={`${index}-${lineIndex}`}>{renderInline(line.slice(2))}</blockquote>
      return <p key={`${index}-${lineIndex}`}>{renderInline(line)}</p>
    })
  })
}

interface Props {
  message: ConversationMessage
}

export function MessageBubble({ message }: Props) {
  const isUser = message.role === 'USER'
  return (
    <article className={`message-row ${isUser ? 'message-row--user' : ''}`}>
      {!isUser && <div className="assistant-avatar">✦</div>}
      <div className={`message-bubble ${isUser ? 'message-bubble--user' : 'message-bubble--assistant'} ${message.status === 'FAILED' ? 'message-bubble--failed' : ''}`}>
        {isUser ? <p>{message.content}</p> : <div className="markdown-content">{renderMarkdown(message.content)}{message.status === 'STREAMING' && <span className="stream-cursor" />}</div>}
        {message.status === 'FAILED' && <small className="message-error">回复未完成</small>}
      </div>
    </article>
  )
}

import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

export function MarkdownEditor({
  value,
  onChange,
}: {
  value: string;
  onChange: (value: string) => void;
}) {
  return (
    <div className="editor-grid">
      <label>
        Markdown 内容
        <textarea value={value} onChange={(event) => onChange(event.target.value)} />
      </label>
      <div>
        <div className="muted" style={{ marginBottom: '0.35rem', fontSize: '0.8rem' }}>
          实时预览
        </div>
        <div className="markdown-preview">
          <ReactMarkdown remarkPlugins={[remarkGfm]}>{value || '在这里输入正文...'}</ReactMarkdown>
        </div>
      </div>
    </div>
  );
}

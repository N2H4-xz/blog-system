import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api, unwrap } from '../lib/api';
import { getErrorMessage } from '../lib/format';
import { usePageTitle } from '../hooks/usePageTitle';
import { setSession } from '../store/auth';
import { AuthShell } from '../components/AuthShell';
import type { AuthPayload } from '../types';

export function RegisterPage() {
  usePageTitle('注册');
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', password: '', displayName: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      const payload = await unwrap<AuthPayload>(api.post('/auth/register', form));
      setSession(payload);
      navigate('/');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthShell
      eyebrow="Create Account"
      title="注册"
      description="创建账号后即可开始写作和管理内容。"
      footer={
        <>
          已有账号？<Link to="/login">登录</Link>
        </>
      }
    >
      <div className="auth-grid">
        <label className="field">
          <span className="field-label">显示名称</span>
          <input value={form.displayName} onChange={(event) => setForm((current) => ({ ...current, displayName: event.target.value }))} />
        </label>
        <label className="field">
          <span className="field-label">用户名</span>
          <input value={form.username} onChange={(event) => setForm((current) => ({ ...current, username: event.target.value }))} />
        </label>
        <label className="field">
          <span className="field-label">密码</span>
          <input type="password" value={form.password} onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))} />
        </label>
        {error && <div className="empty-state">{error}</div>}
        <button className="accent-button button-block" disabled={submitting} onClick={handleSubmit}>
          {submitting ? '注册中...' : '注册'}
        </button>
      </div>
    </AuthShell>
  );
}

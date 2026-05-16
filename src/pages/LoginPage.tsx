import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { api, unwrap } from '../lib/api';
import { getErrorMessage } from '../lib/format';
import { usePageTitle } from '../hooks/usePageTitle';
import { setSession } from '../store/auth';
import { AuthShell } from '../components/AuthShell';
import type { AuthPayload } from '../types';

export function LoginPage() {
  usePageTitle('登录');
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      const payload = await unwrap<AuthPayload>(api.post('/auth/login', form));
      setSession(payload);
      navigate(location.state?.from ?? '/');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthShell
      eyebrow="Welcome Back"
      title="登录"
      description="登录后管理你的文章、评论和个人创作空间。"
      footer={
        <>
          还没有账号？<Link to="/register">注册</Link>
        </>
      }
    >
      <div className="auth-grid">
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
          {submitting ? '登录中...' : '登录'}
        </button>
      </div>
    </AuthShell>
  );
}

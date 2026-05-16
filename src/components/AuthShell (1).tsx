import type { ReactNode } from 'react';

interface AuthShellProps {
  eyebrow: string;
  title: string;
  description: string;
  footer: ReactNode;
  children: ReactNode;
}

export function AuthShell({ eyebrow, title, description, footer, children }: AuthShellProps) {
  return (
    <section className="auth-shell">
      <div className="auth-hero">
        <span className="eyebrow">{eyebrow}</span>
        <h1 className="auth-title">{title}</h1>
        <p className="auth-description">{description}</p>
      </div>
      <div className="auth-card">
        {children}
        <p className="auth-footer">{footer}</p>
      </div>
    </section>
  );
}

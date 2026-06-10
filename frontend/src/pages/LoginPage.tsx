import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate  = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error,    setError]    = useState('');
  const [loading,  setLoading]  = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(username, password);
      navigate('/');           // redirect to main page on success
    } catch {
      setError('Invalid username or password.');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoAdminLogin = async () => {
    setError('');
    setLoading(true);
    try {
      await login('admin', 'admin123');
      navigate('/profile');
    } catch {
      setError('Demo admin account is not available. Restart the backend to seed it.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <span className="logo-icon">&#60;/&#62;</span>
          <h1 className="auth-title">Sign in to DevBoard</h1>
          <p className="auth-subtitle">Welcome back - continue your code review journey</p>
        </div>

        <div className="auth-demo-box">
          <strong>Presentation mode</strong>
          <span>Use the admin demo account to show user roles, categories, and moderation.</span>
          <button
            type="button"
            className="btn btn-ghost btn-sm demo-login-btn"
            onClick={handleDemoAdminLogin}
            disabled={loading}
          >
            Enter Admin Demo
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <label className="auth-label">Username</label>
          <input
            id="login-username"
            className="input-field"
            type="text"
            placeholder="your_username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            autoFocus
          />

          <label className="auth-label">Password</label>
          <input
            id="login-password"
            className="input-field"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />

          {error && <p className="auth-error">{error}</p>}

          <button
            id="login-submit"
            type="submit"
            className="btn btn-primary auth-submit"
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <p className="auth-switch">
          Don't have an account?{' '}
          <Link to="/register" className="auth-link">Register</Link>
        </p>
      </div>
    </div>
  );
}

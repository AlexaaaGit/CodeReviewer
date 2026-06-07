import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate     = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirm,  setConfirm]  = useState('');
  const [error,    setError]    = useState('');
  const [loading,  setLoading]  = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirm) {
      setError('Passwords do not match.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    setLoading(true);
    try {
      await register(username, password);
      navigate('/');           // redirect after successful register + auto-login
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: string } })?.response?.data;
      setError(typeof msg === 'string' ? msg : 'Registration failed. Try a different username.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <span className="logo-icon">&#60;/&#62;</span>
          <h1 className="auth-title">Create an account</h1>
          <p className="auth-subtitle">Join DevBoard and share your projects</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <label className="auth-label">Username</label>
          <input
            id="register-username"
            className="input-field"
            type="text"
            placeholder="choose_a_username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            autoFocus
            minLength={3}
          />

          <label className="auth-label">Password</label>
          <input
            id="register-password"
            className="input-field"
            type="password"
            placeholder="at least 6 characters"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />

          <label className="auth-label">Confirm Password</label>
          <input
            id="register-confirm"
            className="input-field"
            type="password"
            placeholder="repeat your password"
            value={confirm}
            onChange={e => setConfirm(e.target.value)}
            required
          />

          {error && <p className="auth-error">{error}</p>}

          <button
            id="register-submit"
            type="submit"
            className="btn btn-primary auth-submit"
            disabled={loading}
          >
            {loading ? 'Creating account…' : 'Register'}
          </button>
        </form>

        <p className="auth-switch">
          Already have an account?{' '}
          <Link to="/login" className="auth-link">Sign In</Link>
        </p>
      </div>
    </div>
  );
}

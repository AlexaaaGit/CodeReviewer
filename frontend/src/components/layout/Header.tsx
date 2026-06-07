import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

/**
 * Application header with navigation.
 * Shows auth buttons for guests, user info + profile/admin links for authenticated users.
 */
export default function Header() {
  const { user, isAdmin, logout } = useAuth();

  return (
    <header className="header">
      <div className="header-content">
        <Link to="/" className="logo-link">
          <h1 className="logo">
            <span className="logo-icon">&lt;/&gt;</span> DevBoard
          </h1>
        </Link>
        <p className="subtitle">Code Review Marketplace — Get feedback on your projects</p>
      </div>

      {/* Authentication navigation */}
      <nav className="auth-nav">
        {user ? (
          <>
            <span className="nav-user">
              👤 {user.username}
              {user.role === 'ROLE_ADMIN' && <span className="role-badge">Admin</span>}
              {user.role === 'ROLE_MENTOR' && <span className="role-badge role-mentor-badge">Mentor</span>}
              {user.role === 'ROLE_JUNIOR' && <span className="role-badge role-junior-badge">Junior</span>}
            </span>
            {/* Profile link — visible to all authenticated users */}
            <Link to="/profile" className="btn btn-ghost btn-sm">My Profile</Link>
            {/* Admin-only navigation links */}
            {isAdmin && (
              <>
                <Link to="/categories" className="btn btn-ghost btn-sm">Categories</Link>
                <Link to="/admin/users" className="btn btn-ghost btn-sm">Users</Link>
              </>
            )}
            <button className="btn btn-ghost btn-sm" onClick={logout}>Sign Out</button>
          </>
        ) : (
          <>
            <Link to="/login"    className="btn btn-ghost btn-sm">Sign In</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}

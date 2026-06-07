import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { userApi } from '../api/userApi';
import CategoryBadge from '../components/categories/CategoryBadge';
import type { ProductResponse, CommentResponse } from '../types';

/**
 * User Profile Page
 * - JUNIOR: Shows their submitted projects.
 * - MENTOR: Shows their posted code reviews.
 * - ADMIN: Shows a summary + link to Admin Panel.
 */
export default function ProfilePage() {
  const { user, isJunior, isMentor, isAdmin } = useAuth();
  const navigate = useNavigate();

  const [projects, setProjects] = useState<ProductResponse[]>([]);
  const [reviews, setReviews] = useState<CommentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    // Redirect to login if not authenticated
    if (!user) {
      navigate('/login');
      return;
    }

    setLoading(true);
    const requests: Promise<void>[] = [];

    if (isJunior || isAdmin) {
      requests.push(
        userApi.getMyProjects()
          .then(res => setProjects(res.data))
          .catch(() => setError('Failed to load your projects.'))
      );
    }

    if (isMentor || isAdmin) {
      requests.push(
        userApi.getMyComments()
          .then(res => setReviews(res.data))
          .catch(() => setError('Failed to load your reviews.'))
      );
    }

    Promise.all(requests).finally(() => setLoading(false));
  }, [user, isJunior, isMentor, isAdmin, navigate]);

  if (!user) return null;

  const roleFriendly =
    user.role === 'ROLE_ADMIN'  ? 'Admin'  :
    user.role === 'ROLE_MENTOR' ? 'Mentor' : 'Junior Developer';

  return (
    <div className="profile-page">
      {/* ── Profile Header ─────────────────────────────── */}
      <div className="profile-hero">
        <div className="profile-avatar">
          {user.username.charAt(0).toUpperCase()}
        </div>
        <div className="profile-info">
          <h2 className="profile-username">@{user.username}</h2>
          <span className={`profile-role-pill ${
            user.role === 'ROLE_ADMIN'  ? 'role-admin'  :
            user.role === 'ROLE_MENTOR' ? 'role-mentor' : 'role-junior'
          }`}>
            {roleFriendly}
          </span>
        </div>
      </div>

      {error && <p className="auth-error" style={{ marginBottom: '20px' }}>{error}</p>}

      {loading ? (
        <p className="loading-text">Loading your profile…</p>
      ) : (
        <>
          {/* ── Admin Panel Link ───────────────────────── */}
          {isAdmin && (
            <section className="profile-section">
              <h3 className="section-title">⚡ Admin Panel</h3>
              <div className="admin-panel-links">
                <Link to="/admin/users" className="admin-panel-card">
                  <span className="admin-panel-icon">👥</span>
                  <div>
                    <div className="admin-panel-title">Manage Users</div>
                    <div className="admin-panel-desc">View, promote, and manage all registered users</div>
                  </div>
                  <span className="admin-panel-arrow">→</span>
                </Link>
                <Link to="/categories" className="admin-panel-card">
                  <span className="admin-panel-icon">🏷️</span>
                  <div>
                    <div className="admin-panel-title">Manage Categories</div>
                    <div className="admin-panel-desc">Add or remove project categories for the platform</div>
                  </div>
                  <span className="admin-panel-arrow">→</span>
                </Link>
              </div>
            </section>
          )}

          {/* ── My Projects (Junior & Admin) ──────────── */}
          {(isJunior || isAdmin) && (
            <section className="profile-section">
              <h3 className="section-title">
                📁 My Projects
                <span className="badge">{projects.length}</span>
              </h3>

              {projects.length === 0 ? (
                <div className="profile-empty">
                  <p>You haven't submitted any projects yet.</p>
                  <Link to="/" className="btn btn-primary btn-sm" style={{ marginTop: '12px', display: 'inline-block' }}>
                    Submit your first project →
                  </Link>
                </div>
              ) : (
                <div className="profile-cards-grid">
                  {projects.map((product) => (
                    <Link
                      key={product.id}
                      to={`/products/${product.id}`}
                      className="profile-product-card"
                    >
                      <div className="profile-card-header">
                        <span className="profile-card-title">{product.title}</span>
                        <span className="status-badge">
                          {product.commentCount > 0 ? `${product.commentCount} review${product.commentCount > 1 ? 's' : ''}` : 'Awaiting review'}
                        </span>
                      </div>
                      <p className="profile-card-desc">{product.description}</p>
                      {product.categories && product.categories.length > 0 && (
                        <div className="category-badges">
                          {product.categories.map(cat => (
                            <CategoryBadge key={cat.id} category={cat} />
                          ))}
                        </div>
                      )}
                      <div className="profile-card-meta">
                        Submitted {new Date(product.creationDate).toLocaleDateString()}
                      </div>
                    </Link>
                  ))}
                </div>
              )}
            </section>
          )}

          {/* ── My Code Reviews (Mentor & Admin) ─────── */}
          {(isMentor || isAdmin) && (
            <section className="profile-section">
              <h3 className="section-title">
                💬 My Code Reviews
                <span className="badge">{reviews.length}</span>
              </h3>

              {reviews.length === 0 ? (
                <div className="profile-empty">
                  <p>You haven't posted any code reviews yet.</p>
                  <Link to="/" className="btn btn-primary btn-sm" style={{ marginTop: '12px', display: 'inline-block' }}>
                    Browse projects to review →
                  </Link>
                </div>
              ) : (
                <div className="profile-reviews-list">
                  {reviews.map((review) => (
                    <Link
                      key={review.id}
                      to={`/products/${review.productId}`}
                      className="profile-review-card"
                    >
                      <div className="profile-review-header">
                        <span className="verified-mentor-badge">✓ Verified Mentor Review</span>
                        <span className="profile-card-meta">
                          {new Date(review.creationDate).toLocaleDateString()}
                        </span>
                      </div>
                      <p className="profile-review-text">{review.description}</p>
                      <span className="profile-review-link">View project →</span>
                    </Link>
                  ))}
                </div>
              )}
            </section>
          )}
        </>
      )}
    </div>
  );
}

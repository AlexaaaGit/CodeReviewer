import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { productApi } from '../api/productApi';
import { commentApi } from '../api/commentApi';
import CommentList from '../components/comments/CommentList';
import CommentForm from '../components/comments/CommentForm';
import CategoryBadge from '../components/categories/CategoryBadge';
import type { ProductResponse, CommentResponse } from '../types';

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { user, isMentor, isAdmin } = useAuth();
  const productId = Number(id);

  const [product, setProduct] = useState<ProductResponse | null>(null);
  const [comments, setComments] = useState<CommentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchProductAndComments = async () => {
    setLoading(true);
    try {
      const prodRes = await productApi.getById(productId);
      setProduct(prodRes.data);

      const commRes = await commentApi.getByProductId(productId);
      setComments(commRes.data);
    } catch (err) {
      console.error(err);
      setError('Failed to load project details.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (productId) {
      fetchProductAndComments();
    }
  }, [productId]);

  const handleAddComment = (description: string) => {
    commentApi.create(productId, { description })
      .then(() => fetchProductAndComments())
      .catch((err) => console.error('Failed to post review:', err));
  };

  const handleUpdateComment = (commentId: number, description: string) => {
    commentApi.update(productId, commentId, { description })
      .then(() => fetchProductAndComments())
      .catch((err) => console.error('Failed to update review:', err));
  };

  const handleDeleteComment = (commentId: number) => {
    if (window.confirm('Are you sure you want to delete this review?')) {
      commentApi.delete(productId, commentId)
        .then(() => fetchProductAndComments())
        .catch((err) => console.error('Failed to delete review:', err));
    }
  };

  if (loading) return <div className="loading-text">Loading project details...</div>;
  if (error || !product) return <div className="empty-text">{error || 'Project not found.'}</div>;

  return (
    <div className="product-detail-page">
      <Link to="/" className="btn btn-ghost btn-sm back-link">
        ← Back to Projects
      </Link>

      <article className="product-detail-card">
        <div className="card-header">
          <h2 className="detail-title">{product.title}</h2>
          <span className="status-badge">Needs Review</span>
        </div>

        <p className="detail-description">{product.description}</p>

        {product.categories && product.categories.length > 0 && (
          <div className="detail-categories">
            <span className="section-label">Categories:</span>
            <div className="category-badges">
              {product.categories.map((cat) => (
                <CategoryBadge key={cat.id} category={cat} />
              ))}
            </div>
          </div>
        )}

        <div className="detail-meta">
          <span>Submitted: {new Date(product.creationDate).toLocaleDateString()}</span>
          {product.creatorUserId && <span>Creator ID: {product.creatorUserId}</span>}
        </div>
      </article>

      <section className="reviews-section">
        <h3 className="section-title">
          Reviews ({comments.length})
        </h3>

        {user ? (
          (isMentor || isAdmin) && (
            <div className="add-review-box">
              <h4>Leave your review</h4>
              <CommentForm onSubmit={handleAddComment} />
            </div>
          )
        ) : (
          <p className="auth-prompt">
            Please <Link to="/login" className="auth-link">Sign In</Link> to post a review.
          </p>
        )}

        <CommentList
          comments={comments}
          onUpdate={handleUpdateComment}
          onDelete={handleDeleteComment}
        />
      </section>
    </div>
  );
}

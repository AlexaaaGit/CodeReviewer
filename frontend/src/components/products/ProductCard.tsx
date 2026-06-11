import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import CategoryBadge from '../categories/CategoryBadge';
import type { ProductResponse, ProductRequest } from '../../types';

/**
 * Single product card displayed in the product grid.
 * Supports inline editing and soft-delete (admin only).
 */
interface ProductCardProps {
  product: ProductResponse;
  onUpdate: (id: number, data: ProductRequest) => void;
  onDelete: (id: number) => void;
}

export default function ProductCard({ product, onUpdate, onDelete }: ProductCardProps) {
  const { user, isAdmin } = useAuth();

  const [editing, setEditing]           = useState(false);
  const [editTitle, setEditTitle]       = useState(product.title);
  const [editDescription, setEditDescription] = useState(product.description);
  const [editSubmissionType, setEditSubmissionType] = useState(product.submissionType);
  const [editCodeSnippet, setEditCodeSnippet] = useState(product.codeSnippet ?? '');
  const [editSourceUrl, setEditSourceUrl] = useState(product.sourceUrl ?? '');
  const reviewStatus = product.commentCount > 0 ? 'Reviewed' : 'Needs Review';
  const sourceLabel =
    product.submissionType === 'GITHUB' ? 'GitHub' :
    product.submissionType === 'FILE' ? 'File' : 'Pasted code';

  // Save edited product
  const handleSave = () => {
    onUpdate(product.id, {
      title: editTitle,
      description: editDescription,
      imageUrl: product.imageUrl,
      submissionType: editSubmissionType,
      codeSnippet: editCodeSnippet,
      sourceUrl: editSourceUrl,
      categoryIds: product.categories.map((c) => c.id),
    });
    setEditing(false);
  };

  // Confirm and delete product
  const handleDelete = () => {
    if (window.confirm('Are you sure you want to delete this project?')) {
      onDelete(product.id);
    }
  };

  return (
    <div className="product-card">
      {editing ? (
        /* Edit Mode */
        <div className="edit-mode">
          <input
            className="input-field"
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
            placeholder="Project Title"
          />
          <input
            className="input-field"
            value={editDescription}
            onChange={(e) => setEditDescription(e.target.value)}
            placeholder="Description"
          />
          <select
            className="input-field"
            value={editSubmissionType}
            onChange={(e) => setEditSubmissionType(e.target.value as 'PASTE' | 'GITHUB' | 'FILE')}
          >
            <option value="PASTE">Paste code directly</option>
            <option value="GITHUB">GitHub / Gist link</option>
            <option value="FILE">File / raw link</option>
          </select>
          {editSubmissionType === 'PASTE' ? (
            <textarea
              className="input-field code-input"
              value={editCodeSnippet}
              onChange={(e) => setEditCodeSnippet(e.target.value)}
              placeholder="Code for review"
              rows={6}
            />
          ) : (
            <input
              className="input-field"
              value={editSourceUrl}
              onChange={(e) => setEditSourceUrl(e.target.value)}
              placeholder={editSubmissionType === 'GITHUB' ? 'GitHub or Gist URL' : 'File or raw URL'}
            />
          )}
          <div className="card-actions">
            <button onClick={handleSave} className="btn btn-success btn-sm">Save</button>
            <button onClick={() => setEditing(false)} className="btn btn-ghost btn-sm">Cancel</button>
          </div>
        </div>
      ) : (
        /* View Mode */
        <>
          <div className="card-header">
            <Link to={`/products/${product.id}`} className="card-title-link">
              <h3 className="card-title">{product.title}</h3>
            </Link>
            <div className="card-badges">
              <span className="source-badge">{sourceLabel}</span>
              <span className={`status-badge ${product.commentCount > 0 ? 'status-reviewed' : ''}`}>
                {reviewStatus}
              </span>
            </div>
          </div>

          <p className="card-description">{product.description}</p>

          {/* Category badges */}
          {product.categories && product.categories.length > 0 && (
            <div className="category-badges">
              {product.categories.map((cat) => (
                <CategoryBadge key={cat.id} category={cat} />
              ))}
            </div>
          )}

          {/* Comment count */}
          {product.commentCount > 0 && (
            <div className="comments-preview">
              <span className="comments-count">
                {product.commentCount} review{product.commentCount > 1 ? 's' : ''}
              </span>
            </div>
          )}

          {product.sourceUrl && product.submissionType !== 'PASTE' && (
            <div className="source-link-row">
              <a href={product.sourceUrl} target="_blank" rel="noreferrer">
                Open source link
              </a>
            </div>
          )}

          <div className="card-footer">
            <div className="card-meta-stack">
              <span className="card-date">
                {new Date(product.creationDate).toLocaleDateString()}
              </span>
              {product.creatorUsername && (
                <span className="card-author">by @{product.creatorUsername}</span>
              )}
            </div>
            <div className="card-actions">
              {/* View details link */}
              <Link to={`/products/${product.id}`} className="btn btn-ghost btn-sm">
                View
              </Link>
              {/* Edit button — Creator (Junior) or Admin only */}
              {(isAdmin || (user && user.role === 'ROLE_JUNIOR' && product.creatorUserId === user.id)) && (
                <button onClick={() => setEditing(true)} className="btn btn-ghost btn-sm">
                  Edit
                </button>
              )}
              {/* Delete button — ADMIN only */}
              {isAdmin && (
                <button onClick={handleDelete} className="btn btn-danger btn-sm">
                  Delete
                </button>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
}

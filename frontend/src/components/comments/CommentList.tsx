import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import type { CommentResponse } from '../../types';

/**
 * Displays a list of comments for a product.
 * Shows mentor attribution and "Verified Mentor" badge.
 * Supports inline editing and soft-delete (admin only).
 */
interface CommentListProps {
  comments: CommentResponse[];
  onUpdate: (commentId: number, description: string) => void;
  onDelete: (commentId: number) => void;
}

export default function CommentList({ comments, onUpdate, onDelete }: CommentListProps) {
  const { user, isAdmin } = useAuth();

  const [editingId, setEditingId]         = useState<number | null>(null);
  const [editDescription, setEditDescription] = useState('');

  // Enter edit mode for a specific comment
  const startEdit = (comment: CommentResponse) => {
    setEditingId(comment.id);
    setEditDescription(comment.description);
  };

  // Save edited comment
  const handleSave = (commentId: number) => {
    onUpdate(commentId, editDescription);
    setEditingId(null);
  };

  if (comments.length === 0) {
    return <p className="empty-text">No reviews yet. Be the first to leave feedback!</p>;
  }

  return (
    <div className="comment-list">
      {comments.map((comment) => (
        <div key={comment.id} className="comment-card">
          {editingId === comment.id ? (
            /* Edit Mode */
            <div className="edit-mode">
              <textarea
                className="input-field comment-textarea"
                value={editDescription}
                onChange={(e) => setEditDescription(e.target.value)}
                rows={3}
              />
              <div className="card-actions">
                <button onClick={() => handleSave(comment.id)} className="btn btn-success btn-sm">
                  Save
                </button>
                <button onClick={() => setEditingId(null)} className="btn btn-ghost btn-sm">
                  Cancel
                </button>
              </div>
            </div>
          ) : (
            /* View Mode */
            <>
              {/* Reviewer attribution header */}
              <div className="comment-author">
                {comment.authorUsername ? (
                  <>
                    <span className="comment-author-name">@{comment.authorUsername}</span>
                    {comment.authorRole === 'ROLE_MENTOR' && (
                      <span className="verified-mentor-badge">✓ Verified Mentor</span>
                    )}
                    {comment.authorRole === 'ROLE_ADMIN' && (
                      <span className="admin-reviewer-badge">⚡ Admin</span>
                    )}
                  </>
                ) : (
                  <span className="comment-author-name anonymous">Anonymous</span>
                )}
              </div>

              <p className="comment-text">{comment.description}</p>
              <div className="comment-footer">
                <span className="comment-date">
                  {new Date(comment.creationDate).toLocaleDateString()}
                </span>
                <div className="card-actions">
                  {/* Edit — Creator (Mentor) or Admin only */}
                  {(isAdmin || (user && user.role === 'ROLE_MENTOR' && comment.creatorUserId === user.id)) && (
                    <button onClick={() => startEdit(comment)} className="btn btn-ghost btn-sm">
                      ✏️
                    </button>
                  )}
                  {/* Delete — admin only */}
                  {isAdmin && (
                    <button
                      onClick={() => onDelete(comment.id)}
                      className="btn btn-danger btn-sm"
                    >
                      🗑️
                    </button>
                  )}
                </div>
              </div>
            </>
          )}
        </div>
      ))}
    </div>
  );
}

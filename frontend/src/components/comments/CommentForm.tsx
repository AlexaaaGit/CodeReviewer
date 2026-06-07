import { useState } from 'react';

/**
 * Form for adding a new comment to a product.
 */
interface CommentFormProps {
  onSubmit: (description: string) => void;
}

export default function CommentForm({ onSubmit }: CommentFormProps) {
  const [description, setDescription] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!description.trim()) return;
    onSubmit(description);
    setDescription('');
  };

  return (
    <form onSubmit={handleSubmit} className="comment-form">
      <textarea
        className="input-field comment-textarea"
        placeholder="Write your review or feedback..."
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        rows={3}
        required
      />
      <button type="submit" className="btn btn-primary btn-sm">
        💬 Post Review
      </button>
    </form>
  );
}

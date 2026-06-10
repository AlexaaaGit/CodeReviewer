import { useState, useEffect } from 'react';
import { categoryApi } from '../../api/categoryApi';
import type { ProductRequest, CategoryResponse } from '../../types';

/**
 * Form for adding a new product.
 * Includes title, description, and multi-select category assignment.
 */
interface ProductFormProps {
  onSubmit: (data: ProductRequest) => void;
}

export default function ProductForm({ onSubmit }: ProductFormProps) {
  const [title, setTitle]               = useState('');
  const [description, setDescription]   = useState('');
  const [codeSnippet, setCodeSnippet]   = useState('');
  const [selectedCategoryIds, setSelectedCategoryIds] = useState<number[]>([]);
  const [categories, setCategories]     = useState<CategoryResponse[]>([]);

  // Load available categories on mount
  useEffect(() => {
    categoryApi.getAll()
      .then((res) => setCategories(res.data))
      .catch((err) => console.error('Failed to load categories:', err));
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    onSubmit({
      title,
      description,
      codeSnippet,
      categoryIds: selectedCategoryIds,
    });

    // Reset form after submission
    setTitle('');
    setDescription('');
    setCodeSnippet('');
    setSelectedCategoryIds([]);
  };

  // Toggle a category in the selection
  const toggleCategory = (id: number) => {
    setSelectedCategoryIds((prev) =>
      prev.includes(id) ? prev.filter((cid) => cid !== id) : [...prev, id]
    );
  };

  return (
    <section className="form-section">
      <h2 className="section-title">Submit your project for review</h2>
      <form onSubmit={handleSubmit} className="project-form">
        <input
          className="input-field"
          placeholder="Project Title (e.g. 'Netflix Clone in React')"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          className="input-field"
          placeholder="Description & Tech Stack"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <textarea
          className="input-field code-input"
          placeholder="Paste the code you want reviewed..."
          value={codeSnippet}
          onChange={(e) => setCodeSnippet(e.target.value)}
          rows={8}
        />
        <button type="submit" className="btn btn-primary">+ Submit Project</button>
      </form>

      {/* Category selection chips */}
      {categories.length > 0 && (
        <div className="category-chips">
          <span className="chips-label">Categories:</span>
          {categories.map((cat) => (
            <button
              key={cat.id}
              type="button"
              className={`chip ${selectedCategoryIds.includes(cat.id) ? 'chip-active' : ''}`}
              onClick={() => toggleCategory(cat.id)}
            >
              {cat.name}
            </button>
          ))}
        </div>
      )}
    </section>
  );
}

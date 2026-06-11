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

interface FormErrors {
  title?: string;
  source?: string;
}

export default function ProductForm({ onSubmit }: ProductFormProps) {
  const [title, setTitle]               = useState('');
  const [description, setDescription]   = useState('');
  const [submissionType, setSubmissionType] = useState<'PASTE' | 'GITHUB' | 'FILE'>('PASTE');
  const [codeSnippet, setCodeSnippet]   = useState('');
  const [sourceUrl, setSourceUrl]       = useState('');
  const [selectedCategoryIds, setSelectedCategoryIds] = useState<number[]>([]);
  const [categories, setCategories]     = useState<CategoryResponse[]>([]);
  const [errors, setErrors]             = useState<FormErrors>({});

  // Load available categories on mount
  useEffect(() => {
    categoryApi.getAll()
      .then((res) => setCategories(res.data))
      .catch((err) => console.error('Failed to load categories:', err));
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const nextErrors: FormErrors = {};
    const trimmedTitle = title.trim();
    const trimmedCode = codeSnippet.trim();
    const trimmedUrl = sourceUrl.trim();

    if (!trimmedTitle) {
      nextErrors.title = 'Title is required';
    }
    if (submissionType === 'PASTE' && !trimmedCode) {
      nextErrors.source = 'Code is required';
    }
    if (submissionType !== 'PASTE') {
      if (!trimmedUrl) {
        nextErrors.source = 'Code link is required';
      } else if (!isValidHttpUrl(trimmedUrl)) {
        nextErrors.source = 'Enter a valid HTTP or HTTPS URL';
      }
    }

    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    onSubmit({
      title: trimmedTitle,
      description: description.trim(),
      submissionType,
      codeSnippet: submissionType === 'PASTE' ? trimmedCode : '',
      sourceUrl: submissionType === 'PASTE' ? '' : trimmedUrl,
      categoryIds: selectedCategoryIds,
    });

    // Reset form after submission
    setTitle('');
    setDescription('');
    setSubmissionType('PASTE');
    setCodeSnippet('');
    setSourceUrl('');
    setSelectedCategoryIds([]);
    setErrors({});
  };

  const isValidHttpUrl = (value: string) => {
    try {
      const url = new URL(value);
      return url.protocol === 'http:' || url.protocol === 'https:';
    } catch {
      return false;
    }
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
          className={`input-field ${errors.title ? 'input-error' : ''}`}
          placeholder="Project Title (e.g. 'Netflix Clone in React')"
          value={title}
          onChange={(e) => {
            setTitle(e.target.value);
            if (errors.title) setErrors((current) => ({ ...current, title: undefined }));
          }}
          required
        />
        {errors.title && <p className="field-error">{errors.title}</p>}
        <input
          className="input-field"
          placeholder="Description & Tech Stack"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <label className="auth-label">Code source</label>
        <select
          className="input-field"
          value={submissionType}
          onChange={(e) => {
            setSubmissionType(e.target.value as 'PASTE' | 'GITHUB' | 'FILE');
            setErrors((current) => ({ ...current, source: undefined }));
          }}
        >
          <option value="PASTE">Paste code directly</option>
          <option value="GITHUB">GitHub / Gist link</option>
          <option value="FILE">File / raw link</option>
        </select>

        {submissionType === 'PASTE' ? (
          <textarea
            className={`input-field code-input ${errors.source ? 'input-error' : ''}`}
            placeholder="Paste the code you want reviewed..."
            value={codeSnippet}
            onChange={(e) => {
              setCodeSnippet(e.target.value);
              if (errors.source) setErrors((current) => ({ ...current, source: undefined }));
            }}
            rows={8}
          />
        ) : (
          <input
            className={`input-field ${errors.source ? 'input-error' : ''}`}
            type="url"
            placeholder={submissionType === 'GITHUB' ? 'https://github.com/...' : 'https://example.com/file.txt'}
            value={sourceUrl}
            onChange={(e) => {
              setSourceUrl(e.target.value);
              if (errors.source) setErrors((current) => ({ ...current, source: undefined }));
            }}
          />
        )}
        {errors.source && <p className="field-error">{errors.source}</p>}
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

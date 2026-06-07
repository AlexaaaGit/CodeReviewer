import { useEffect, useState } from 'react';
import { categoryApi } from '../../api/categoryApi';
import type { CategoryResponse } from '../../types';

/**
 * Combobox (dropdown) for filtering products by category.
 * Loads categories from the server on mount.
 */
interface CategorySelectProps {
  value: number | null;
  onChange: (categoryId: number | null) => void;
}

export default function CategorySelect({ value, onChange }: CategorySelectProps) {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);

  // Load categories on mount
  useEffect(() => {
    categoryApi.getAll()
      .then((res) => setCategories(res.data))
      .catch((err) => console.error('Failed to load categories:', err));
  }, []);

  return (
    <select
      className="input-field category-select"
      value={value ?? ''}
      onChange={(e) => {
        const val = e.target.value;
        onChange(val ? Number(val) : null);
      }}
    >
      <option value="">All Categories</option>
      {categories.map((cat) => (
        <option key={cat.id} value={cat.id}>
          {cat.name}
        </option>
      ))}
    </select>
  );
}

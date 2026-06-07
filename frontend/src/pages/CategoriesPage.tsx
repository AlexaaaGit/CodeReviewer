import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { categoryApi } from '../api/categoryApi';
import type { CategoryResponse } from '../types';

export default function CategoriesPage() {
  const { isAdmin } = useAuth();
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [name, setName] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchCategories = () => {
    setLoading(true);
    categoryApi.getAll()
      .then((res) => {
        setCategories(res.data);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to load categories.');
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;

    categoryApi.create({ name: name.trim() })
      .then(() => {
        setName('');
        fetchCategories();
      })
      .catch((err) => console.error('Failed to create category:', err));
  };

  const handleSave = (id: number) => {
    if (!editName.trim()) return;

    categoryApi.update(id, { name: editName.trim() })
      .then(() => {
        setEditingId(null);
        fetchCategories();
      })
      .catch((err) => console.error('Failed to update category:', err));
  };

  const handleDelete = (id: number) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      categoryApi.delete(id)
        .then(() => fetchCategories())
        .catch((err) => console.error('Failed to delete category:', err));
    }
  };

  const startEdit = (cat: CategoryResponse) => {
    setEditingId(cat.id);
    setEditName(cat.name);
  };

  if (!isAdmin) {
    return <div className="empty-text">Access Denied. Admin privileges required.</div>;
  }

  return (
    <div className="categories-page">
      <h2 className="page-title">Category Management (Admin)</h2>

      {/* Add category form */}
      <section className="form-section category-form-section">
        <h3 className="section-title">Create new category</h3>
        <form onSubmit={handleAdd} className="add-form">
          <input
            className="input-field"
            placeholder="Category name (e.g. Mobile, DevOps)"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-primary">Create Category</button>
        </form>
      </section>

      {/* Categories listing */}
      <section className="categories-list-section">
        {loading ? (
          <p className="loading-text">Loading categories...</p>
        ) : error ? (
          <p className="error-text">{error}</p>
        ) : categories.length === 0 ? (
          <p className="empty-text">No categories found.</p>
        ) : (
          <div className="categories-table-container">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th className="actions-header">Actions</th>
                </tr>
              </thead>
              <tbody>
                {categories.map((cat) => (
                  <tr key={cat.id}>
                    <td>{cat.id}</td>
                    <td>
                      {editingId === cat.id ? (
                        <input
                          className="input-field table-input"
                          value={editName}
                          onChange={(e) => setEditName(e.target.value)}
                          required
                        />
                      ) : (
                        cat.name
                      )}
                    </td>
                    <td className="actions-cell">
                      {editingId === cat.id ? (
                        <>
                          <button onClick={() => handleSave(cat.id)} className="btn btn-success btn-xs">Save</button>
                          <button onClick={() => setEditingId(null)} className="btn btn-ghost btn-xs">Cancel</button>
                        </>
                      ) : (
                        <>
                          <button onClick={() => startEdit(cat)} className="btn btn-ghost btn-xs">✏️ Edit</button>
                          <button onClick={() => handleDelete(cat.id)} className="btn btn-danger btn-xs">🗑️ Delete</button>
                        </>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

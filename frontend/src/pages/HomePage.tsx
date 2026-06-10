import { useEffect, useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { productApi } from '../api/productApi';
import ProductCard from '../components/products/ProductCard';
import ProductForm from '../components/products/ProductForm';
import SearchBar from '../components/common/SearchBar';
import Pagination from '../components/common/Pagination';
import CategorySelect from '../components/categories/CategorySelect';
import type { ProductResponse, ProductRequest } from '../types';

const PAGE_SIZE = 6;

/**
 * Home page — displays the product list with search, category filter, and pagination.
 * Authenticated users can also submit new products.
 */
export default function HomePage() {
  const { user, isJunior, isAdmin } = useAuth();

  const [products, setProducts] = useState<ProductResponse[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);

  // Fetch products with current filters and pagination
  const fetchProducts = useCallback((page: number, search: string, catId: number | null) => {
    setLoading(true);
    productApi.getAll(page, PAGE_SIZE, search || undefined, catId || undefined)
      .then((response) => {
        setProducts(response.data.content ?? []);
        setTotalPages(response.data.totalPages ?? 0);
        if (response.data.number !== undefined && response.data.number !== page) {
          setCurrentPage(response.data.number);
        }
      })
      .catch((error) => {
        console.error('Failed to load products:', error);
        setProducts([]);
      })
      .finally(() => setLoading(false));
  }, []);

  // Re-fetch when page, search, or category filter changes
  useEffect(() => {
    fetchProducts(currentPage, searchTerm, categoryId);
  }, [currentPage, searchTerm, categoryId, fetchProducts]);

  // Handle search input change — reset to first page
  const handleSearchChange = (value: string) => {
    setSearchTerm(value);
    setCurrentPage(0);
  };

  // Handle category filter change — reset to first page
  const handleCategoryChange = (catId: number | null) => {
    setCategoryId(catId);
    setCurrentPage(0);
  };

  // Add a new product
  const handleAddProduct = (data: ProductRequest) => {
    productApi.create(data)
      .then(() => fetchProducts(0, searchTerm, categoryId))
      .catch((err) => console.error('Failed to add product:', err));
  };

  // Update an existing product
  const handleUpdateProduct = (id: number, data: ProductRequest) => {
    productApi.update(id, data)
      .then(() => fetchProducts(currentPage, searchTerm, categoryId))
      .catch((err) => console.error('Failed to update product:', err));
  };

  // Soft-delete a product (admin only)
  const handleDeleteProduct = (id: number) => {
    productApi.delete(id)
      .then(() => fetchProducts(currentPage, searchTerm, categoryId))
      .catch((err) => console.error('Failed to delete product:', err));
  };

  return (
    <main className="main-content">
      {/* Add Product Form — only for Junior or Admin users */}
      {user && (isJunior || isAdmin) && <ProductForm onSubmit={handleAddProduct} />}

      {/* Product List */}
      <section className="products-section">
        <h2 className="section-title">
          Projects
          <span className="badge">{products.length}</span>
        </h2>

        {/* Search and category filter bar */}
        <div className="filter-bar">
          <SearchBar
            value={searchTerm}
            onChange={handleSearchChange}
            placeholder="Search by project name..."
          />
          <CategorySelect
            value={categoryId}
            onChange={handleCategoryChange}
          />
        </div>

        {loading ? (
          <p className="loading-text">Loading projects...</p>
        ) : !products || products.length === 0 ? (
          <p className="empty-text">No projects found. Try a different search or be the first to submit!</p>
        ) : (
          <div className="products-grid">
            {products.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
                onUpdate={handleUpdateProduct}
                onDelete={handleDeleteProduct}
              />
            ))}
          </div>
        )}

        {/* Pagination controls */}
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </section>
    </main>
  );
}

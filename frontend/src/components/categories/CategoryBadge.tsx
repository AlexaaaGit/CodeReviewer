import type { CategoryResponse } from '../../types';

/**
 * Small badge displaying a category name on a product card.
 */
interface CategoryBadgeProps {
  category: CategoryResponse;
}

export default function CategoryBadge({ category }: CategoryBadgeProps) {
  return (
    <span className="category-badge">
      {category.name}
    </span>
  );
}

/**
 * Reusable pagination controls.
 * Shows Prev/Next buttons and current page info.
 */
interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export default function Pagination({ currentPage, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null;

  return (
    <div className="pagination">
      <button
        className="btn btn-ghost"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
      >
        ← Prev
      </button>
      <span className="pagination-info">
        Page {currentPage + 1} of {totalPages}
      </span>
      <button
        className="btn btn-ghost"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage >= totalPages - 1}
      >
        Next →
      </button>
    </div>
  );
}

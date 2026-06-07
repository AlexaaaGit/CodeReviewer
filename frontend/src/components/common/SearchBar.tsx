/**
 * Reusable search input component.
 * Calls onChange whenever the user types, enabling real-time search.
 */
interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}

export default function SearchBar({ value, onChange, placeholder }: SearchBarProps) {
  return (
    <div className="search-bar">
      <input
        className="input-field"
        placeholder={placeholder || '🔍 Search...'}
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
}

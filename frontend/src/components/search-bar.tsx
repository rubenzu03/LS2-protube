import { useState } from 'react';
import { useNavigate } from 'react-router';
import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';

export function SearchBar() {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const trimmedQuery = query.trim();
    if (!trimmedQuery) return;
    navigate(`/search?q=${encodeURIComponent(trimmedQuery)}`);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="flex items-stretch rounded-full border w-96 bg-secondary/20 overflow-hidden"
    >
      <input
        type="text"
        placeholder="Search"
        className="flex-1 bg-transparent px-4 py-2 outline-none placeholder:text-muted-foreground"
        value={query}
        onChange={(event) => setQuery(event.target.value)}
      />
      <button
        type="submit"
        className="flex items-center justify-center border-l px-4 cursor-pointer bg-accent"
      >
        <MagnifyingGlassIcon className="h-5 w-5" />
      </button>
    </form>
  );
}

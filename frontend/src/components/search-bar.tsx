import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';

export function SearchBar() {
  return (
    <div className="flex items-stretch rounded-full border w-96 bg-secondary/20 overflow-hidden ">
      <input type="text" placeholder="Search" className="flex-1 bg-transparent px-4 py-2 outline-none placeholder:text-muted-foreground" />
      <button className="flex items-center justify-center border-l px-4 cursor-pointer bg-accent">
        <MagnifyingGlassIcon className="h-5 w-5" />
      </button>
    </div>
  );
}

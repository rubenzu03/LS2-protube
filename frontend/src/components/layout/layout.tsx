import type { ReactNode } from 'react';
import { Link } from 'react-router';
import { useEffect, useState } from 'react';

import { SearchBar } from '../search-bar';
import { UserButton } from '@/components/user-button';
import { cn } from '@/lib/utils';

type Props = {
  children: ReactNode;
};

export function Layout({ children }: Props) {
  const [hideBar, setHideBar] = useState(false);
  useEffect(() => {
    const handleScroll = () => {
      const scrollTop = window.scrollY;
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      const scrollPercent = Math.floor((scrollTop / docHeight) * 100);

      if (scrollPercent > 10) {
        setHideBar(true);
      } else {
        setHideBar(false);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <div className="flex min-h-screen flex-col bg-background text-foreground">
      <header
        className={cn(
          'sticky top-0 z-50 border-border bg-background/80 backdrop-blur-sm p-3 transition-all duration-300',
          hideBar ? 'border-b-0' : 'border-b'
        )}
      >
        <div className="flex w-full justify-between items-center gap-3 px-36">
          <Link to="/" className="flex items-center gap-3">
            <img src="/protube-logo-removebg-preview.png" alt="ProTube Logo" className="h-8 w-auto" />
            <h1 className="flex-1 text-xl font-semibold tracking-tight text-indigo-600">ProTube</h1>
          </Link>
          <SearchBar />
          <UserButton />
        </div>
      </header>

      <main className="flex flex-1 flex-col px-36 py-6">{children}</main>
    </div>
  );
}

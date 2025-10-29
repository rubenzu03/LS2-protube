import type { ReactNode } from 'react';
import { ModeToggle } from '../mode-toggle';
import { Link } from 'react-router';

type Props = {
  children: ReactNode;
};

export function Layout({ children }: Props) {
  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="sticky top-0 z-50 border-b border-border bg-background/80 backdrop-blur-sm">
        <Link to="/">
          <div className="mx-auto flex max-w-[1400px] gap-3 px-6 py-4">
            <img src="/protube-logo-removebg-preview.png" alt="ProTube Logo" className="h-8 w-auto" />
            <h1 className="flex-1 text-xl font-semibold tracking-tight text-indigo-600">ProTube</h1>
            <ModeToggle />
          </div>
        </Link>
      </header>

      <main>{children}</main>
    </div>
  );
}

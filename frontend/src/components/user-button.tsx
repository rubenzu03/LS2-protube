import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router';
import { User as UserIcon, UploadCloud } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu';
import { ModeToggle } from '@/components/mode-toggle';
import { api, clearAuthToken } from '@/utils/api';

const TOKEN_KEY = 'protube_token';

function getUsernameFromToken(token: string | null): string | null {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload));
    return decoded.sub || null;
  } catch {
    return null;
  }
}

function isTokenValid(token: string | null): boolean {
  if (!token) return false;
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return false;
    const payload = JSON.parse(atob(parts[1]));
    // Check if token has expired
    if (payload.exp && payload.exp * 1000 < Date.now()) return false;
    // Check if token has a subject (username)
    if (!payload.sub) return false;
    return true;
  } catch {
    return false;
  }
}

export function UserButton() {
  const [hasToken, setHasToken] = useState<boolean>(() => {
    if (typeof window === 'undefined') return false;
    const token = window.localStorage.getItem(TOKEN_KEY);
    return isTokenValid(token);
  });
  const [username, setUsername] = useState<string | null>(() => {
    if (typeof window === 'undefined') return null;
    const token = window.localStorage.getItem(TOKEN_KEY);
    if (!isTokenValid(token)) return null;
    return getUsernameFromToken(token);
  });
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token && isTokenValid(token)) {
      api.defaults.headers.common.Authorization = `Bearer ${token}`;
      setHasToken(true);
      setUsername(getUsernameFromToken(token));
    } else {
      // Invalid or no token - clear everything
      localStorage.removeItem(TOKEN_KEY);
      delete api.defaults.headers.common.Authorization;
      setHasToken(false);
      setUsername(null);
    }

    const handleStorage = (event: StorageEvent) => {
      if (event.key === TOKEN_KEY) {
        const value = event.newValue;
        if (value && isTokenValid(value)) {
          api.defaults.headers.common.Authorization = `Bearer ${value}`;
          setHasToken(true);
          setUsername(getUsernameFromToken(value));
        } else {
          localStorage.removeItem(TOKEN_KEY);
          delete api.defaults.headers.common.Authorization;
          setHasToken(false);
          setUsername(null);
        }
      }
    };
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  const handleSignIn = () => {
    navigate('/auth', { state: { from: location.pathname + location.search } });
  };

  const handleSignOut = () => {
    clearAuthToken();
    setHasToken(false);
    setUsername(null);
    navigate('/', { replace: true });
  };

  const handleUpload = () => {
    navigate('/upload');
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        {hasToken && username ? (
          <button
            className="h-9 w-9 rounded-full bg-gradient-to-br from-indigo-600 to-indigo-800 flex items-center justify-center text-white text-sm font-bold hover:opacity-80 transition-opacity"
            aria-label="User menu"
          >
            {username[0]?.toUpperCase() || '?'}
          </button>
        ) : (
          <Button variant="outline" size="icon" aria-label="User menu">
            <UserIcon className="h-5 w-5" />
          </Button>
        )}
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuLabel className="flex flex-col gap-0.5">
          <span className="text-xs font-medium text-muted-foreground">
            {hasToken ? `Signed in as ${username}` : 'You are not signed in'}
          </span>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        {hasToken && (
          <>
            <DropdownMenuItem
              onSelect={(event) => {
                event.preventDefault();
                handleUpload();
              }}
            >
              <UploadCloud className="mr-2 h-4 w-4" />
              <span>Upload video</span>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
          </>
        )}
        <DropdownMenuItem asChild>
          <ModeToggle />
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        {hasToken ? (
          <DropdownMenuItem
            onSelect={(event) => {
              event.preventDefault();
              handleSignOut();
            }}
            variant="destructive"
          >
            Sign out
          </DropdownMenuItem>
        ) : (
          <DropdownMenuItem
            onSelect={(event) => {
              event.preventDefault();
              handleSignIn();
            }}
          >
            Sign in
          </DropdownMenuItem>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

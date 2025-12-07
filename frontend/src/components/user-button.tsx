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

export function UserButton() {
  const [hasToken, setHasToken] = useState<boolean>(() => {
    return typeof window !== 'undefined' && !!window.localStorage.getItem(TOKEN_KEY);
  });
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      api.defaults.headers.common.Authorization = `Bearer ${token}`;
      setHasToken(true);
    } else {
      delete api.defaults.headers.common.Authorization;
      setHasToken(false);
    }

    const handleStorage = (event: StorageEvent) => {
      if (event.key === TOKEN_KEY) {
        const value = event.newValue;
        if (value) {
          api.defaults.headers.common.Authorization = `Bearer ${value}`;
          setHasToken(true);
        } else {
          delete api.defaults.headers.common.Authorization;
          setHasToken(false);
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
    navigate('/', { replace: true });
  };

  const handleUpload = () => {
    navigate('/upload');
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="icon" aria-label="User menu">
          <UserIcon className="h-5 w-5" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuLabel className="flex flex-col gap-0.5">
          <span className="text-xs font-medium text-muted-foreground">
            {hasToken ? 'Signed in' : 'You are not signed in'}
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

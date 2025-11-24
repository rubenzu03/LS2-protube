import { useState, type FormEvent } from 'react';
import { useNavigate, useLocation } from 'react-router';
import axios from 'axios';

import { Layout } from '@/components/layout/layout';
import { Button } from '@/components/ui/button';
import { getEnv } from '@/utils/env';
import { api } from '@/utils/api';
import { useDocumentTitle } from '@/hooks/use-document-title';

type Mode = 'signin' | 'signup';

type AuthResponse = {
  token: string;
};

export function AuthPage() {
  const [mode, setMode] = useState<Mode>('signin');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();
  const location = useLocation();

  useDocumentTitle(mode === 'signin' ? 'ProTube - Sign in' : 'ProTube - Create account');

  const { API_DOMAIN } = getEnv();
  const authBaseUrl = `${API_DOMAIN}`;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    if (mode === 'signup' && password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    if (!username.trim() || !password.trim()) {
      setError('Please fill in all required fields.');
      return;
    }

    setIsSubmitting(true);
    try {
      const endpoint = mode === 'signin' ? '/auth/login' : '/auth/register';
      const res = await axios.post<AuthResponse>(`${authBaseUrl}${endpoint}`, {
        username: username.trim(),
        password
      });

      const token = res.data.token;
      if (token) {
        localStorage.setItem('protube_token', token);
        api.defaults.headers.common.Authorization = `Bearer ${token}`;
      }

      const from = (location.state as { from?: string } | null)?.from ?? '/';
      navigate(from, { replace: true });
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        if (err.response?.status === 401) {
          setError('Incorrect username or password.');
        } else if (err.response?.status === 409) {
          setError('An account with this username already exists.');
        } else {
          setError('Could not contact the server. Please try again.');
        }
      } else {
        setError('Something went wrong. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Layout>
      <div className="flex w-full justify-center py-10">
        <div className="flex w-full max-w-xl flex-col gap-8 rounded-2xl border bg-card px-10 py-8 shadow-xl">
          <div className="flex items-center gap-3">
            <img src="/protube-logo-removebg-preview.png" alt="ProTube" className="h-8 w-auto" />
            <div className="flex flex-col">
              <span className="text-lg font-semibold">Sign {mode === 'signin' ? 'in' : 'up'}</span>
              <span className="text-xs text-muted-foreground">to continue to ProTube</span>
            </div>
          </div>

          <div className="relative flex gap-2 rounded-full bg-muted/60 p-1 text-sm">
            <div
              className={`absolute inset-1 w-1/2 rounded-full bg-background shadow-sm transition-transform duration-300 ease-out ${
                mode === 'signin' ? 'translate-x-0' : 'translate-x-full'
              }`}
            />
            <button
              type="button"
              onClick={() => setMode('signin')}
              className={`relative z-10 flex-1 rounded-full px-3 py-1.5 text-center text-sm font-medium transition-colors ${
                mode === 'signin'
                  ? 'text-foreground'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              Sign in
            </button>
            <button
              type="button"
              onClick={() => setMode('signup')}
              className={`relative z-10 flex-1 rounded-full px-3 py-1.5 text-center text-sm font-medium transition-colors ${
                mode === 'signup'
                  ? 'text-foreground'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              Create account
            </button>
          </div>

          <form className="flex flex-col gap-5" onSubmit={handleSubmit}>
            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-medium text-muted-foreground">Username</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full rounded-md border bg-background px-3 py-2 text-sm outline-none focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring"
                placeholder="Your username"
                autoComplete="username"
              />
            </div>

            <div className="flex flex-col gap-1.5">
              <label className="text-xs font-medium text-muted-foreground">Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full rounded-md border bg-background px-3 py-2 text-sm outline-none focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring"
                placeholder="Enter your password"
                autoComplete={mode === 'signin' ? 'current-password' : 'new-password'}
              />
            </div>

            {mode === 'signup' && (
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-medium text-muted-foreground">Confirm password</label>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="w-full rounded-md border bg-background px-3 py-2 text-sm outline-none focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring"
                  placeholder="Repeat your password"
                  autoComplete="new-password"
                />
              </div>
            )}

            {error && (
              <p className="text-xs text-destructive bg-destructive/10 border border-destructive/40 rounded-md px-3 py-2">
                {error}
              </p>
            )}

            <div className="mt-2 flex items-center justify-between">
              <p className="text-xs text-muted-foreground max-w-[60%]">
                {mode === 'signin'
                  ? 'Use your ProTube account to access your personalised video feed.'
                  : 'Create a ProTube account to save your preferences and access all videos.'}
              </p>
              <Button type="submit" disabled={isSubmitting} className="min-w-[110px]">
                {isSubmitting
                  ? mode === 'signin'
                    ? 'Signing in...'
                    : 'Creating...'
                  : mode === 'signin'
                    ? 'Sign in'
                    : 'Create account'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
}

import { render, screen, act, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { UserButton } from '@/components/user-button';
import { api } from '@/utils/api';

const mockNavigate = jest.fn();

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useNavigate: () => mockNavigate,
  useLocation: () => ({ pathname: '/test', search: '' })
}));

jest.mock('@/components/mode-toggle', () => ({
  ModeToggle: () => <div>Mode Toggle</div>
}));

// Create a valid JWT token for testing
// Format: header.payload.signature
function createMockToken(username: string, expiresInSeconds = 3600): string {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = btoa(JSON.stringify({
    sub: username,
    exp: Math.floor(Date.now() / 1000) + expiresInSeconds
  }));
  const signature = 'mock-signature';
  return `${header}.${payload}.${signature}`;
}

describe('UserButton', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
    delete api.defaults.headers.common.Authorization;
  });

  it('renders user button', () => {
    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    expect(screen.getByRole('button', { name: /user menu/i })).toBeInTheDocument();
  });

  it('sets authorization header when token is found on mount', async () => {
    const testToken = createMockToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(api.defaults.headers.common.Authorization).toBe(`Bearer ${testToken}`);
    });
  });

  it('handles storage event when token is added', async () => {
    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    const newToken = createMockToken('newuser');
    act(() => {
      const storageEvent = new StorageEvent('storage', {
        key: 'protube_token',
        newValue: newToken
      });
      window.dispatchEvent(storageEvent);
    });

    await waitFor(() => {
      expect(api.defaults.headers.common.Authorization).toBe(`Bearer ${newToken}`);
    });
  });

  it('handles storage event when token is removed', async () => {
    const testToken = createMockToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    act(() => {
      const storageEvent = new StorageEvent('storage', {
        key: 'protube_token',
        newValue: null
      });
      window.dispatchEvent(storageEvent);
    });

    await waitFor(() => {
      expect(api.defaults.headers.common.Authorization).toBeUndefined();
    });
  });

  it('removes authorization header when there is no token', async () => {
    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(api.defaults.headers.common.Authorization).toBeUndefined();
    });
  });
});

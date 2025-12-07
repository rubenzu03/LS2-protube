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
    localStorage.setItem('protube_token', 'test-token');

    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(api.defaults.headers.common.Authorization).toBe('Bearer test-token');
    });
  });

  it('handles storage event when token is added', async () => {
    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    act(() => {
      const storageEvent = new StorageEvent('storage', {
        key: 'protube_token',
        newValue: 'new-token'
      });
      window.dispatchEvent(storageEvent);
    });

    await waitFor(() => {
      expect(api.defaults.headers.common.Authorization).toBe('Bearer new-token');
    });
  });

  it('handles storage event when token is removed', async () => {
    localStorage.setItem('protube_token', 'test-token');

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

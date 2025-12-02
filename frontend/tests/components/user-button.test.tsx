import { render, screen, act } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { UserButton } from '@/components/user-button';
import { api } from '@/utils/api';

const mockNavigate = jest.fn();

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useNavigate: () => mockNavigate,
  useLocation: () => ({ pathname: '/test', search: '' }),
}));

jest.mock('@/components/mode-toggle', () => ({
  ModeToggle: () => <div>Mode Toggle</div>,
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

  it('sets authorization header when token is found on mount', () => {
    localStorage.setItem('protube_token', 'test-token');

    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    expect(api.defaults.headers.common.Authorization).toBe('Bearer test-token');
  });

  it('handles storage event when token is added', () => {
    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    act(() => {
      const storageEvent = new StorageEvent('storage', {
        key: 'protube_token',
        newValue: 'new-token',
      });
      window.dispatchEvent(storageEvent);
    });

    expect(api.defaults.headers.common.Authorization).toBe('Bearer new-token');
  });

  it('handles storage event when token is removed', () => {
    localStorage.setItem('protube_token', 'test-token');

    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    act(() => {
      const storageEvent = new StorageEvent('storage', {
        key: 'protube_token',
        newValue: null,
      });
      window.dispatchEvent(storageEvent);
    });

    expect(api.defaults.headers.common.Authorization).toBeUndefined();
  });

  it('removes authorization header when there is no token', () => {
    render(
      <BrowserRouter>
        <UserButton />
      </BrowserRouter>
    );

    expect(api.defaults.headers.common.Authorization).toBeUndefined();
  });
});

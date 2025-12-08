import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import axios from 'axios';
import { AuthPage } from '@/components/pages/auth';
import { api } from '@/utils/api';
import { getEnv } from '@/utils/Env';

jest.mock('@/utils/Env');
jest.mock('@/utils/api');
jest.mock('@/hooks/use-document-title');
jest.mock('@/components/layout/layout', () => ({
  Layout: ({ children }: { children: React.ReactNode }) => <div>{children}</div>
}));
jest.mock('axios');

const mockNavigate = jest.fn();
const mockUseLocation = jest.fn(() => ({ pathname: '/auth', search: '', state: null }));

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useNavigate: () => mockNavigate,
  useLocation: () => mockUseLocation()
}));

describe('AuthPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    (getEnv as jest.Mock).mockReturnValue({
      API_DOMAIN: 'http://localhost:8080'
    });
    (axios.post as jest.Mock) = jest.fn();
    (axios.isAxiosError as jest.Mock) = jest.fn((err) => err?.isAxiosError === true);
  });

  it('renders sign in form by default', () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Sign in', { selector: 'span' })).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Your username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter your password')).toBeInTheDocument();
  });

  it('switches to sign up mode when Create account button is clicked', () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const signUpButton = screen.getAllByRole('button', { name: /create account/i })[0];
    fireEvent.click(signUpButton);

    expect(screen.getByText('Create account', { selector: 'span' })).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Repeat your password')).toBeInTheDocument();
  });

  it('switches back to sign in mode when Sign in button is clicked', () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    // Switch to sign up
    fireEvent.click(screen.getAllByRole('button', { name: /create account/i })[0]);
    expect(screen.getByText('Create account', { selector: 'span' })).toBeInTheDocument();

    // Switch back to sign in
    fireEvent.click(screen.getAllByRole('button', { name: /sign in/i })[0]);
    expect(screen.getByText('Sign in', { selector: 'span' })).toBeInTheDocument();
    expect(screen.queryByPlaceholderText('Repeat your password')).not.toBeInTheDocument();
  });

  it('updates username input value', () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username') as HTMLInputElement;
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });

    expect(usernameInput.value).toBe('testuser');
  });

  it('updates password input value', () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const passwordInput = screen.getByPlaceholderText('Enter your password') as HTMLInputElement;
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    expect(passwordInput.value).toBe('password123');
  });

  it('shows confirm password field in sign up mode', () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getAllByRole('button', { name: /create account/i })[0]);

    expect(screen.getByPlaceholderText('Repeat your password')).toBeInTheDocument();
  });

  it('shows error when passwords do not match in sign up mode', async () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const modeToggleButton = screen.getAllByRole('button', { name: /create account/i })[0];
    fireEvent.click(modeToggleButton);

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInputs = screen.getAllByPlaceholderText(/password/i);
    const passwordInput = passwordInputs[0];
    const confirmPasswordInput = screen.getByPlaceholderText('Repeat your password');
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'different' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Passwords do not match.')).toBeInTheDocument();
    });
  });

  it('shows error when fields are empty', async () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const form = screen.getByPlaceholderText('Your username').closest('form')!;
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Please fill in all required fields.')).toBeInTheDocument();
    });
  });

  it('shows error when username is only whitespace', async () => {
    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getAllByPlaceholderText(/password/i)[0];
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: '   ' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Please fill in all required fields.')).toBeInTheDocument();
    });
  });

  it('successfully signs in and navigates', async () => {
    const mockToken = 'test-token-123';
    (axios.post as jest.Mock).mockResolvedValue({
      data: { token: mockToken }
    });

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getAllByPlaceholderText(/password/i)[0];
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        'http://localhost:8080/auth/login',
        {
          username: 'testuser',
          password: 'password123'
        }
      );
      expect(localStorage.getItem('protube_token')).toBe(mockToken);
      expect(api.defaults.headers.common.Authorization).toBe(`Bearer ${mockToken}`);
      expect(mockNavigate).toHaveBeenCalledWith('/', { replace: true });
    });
  });

  it('successfully signs up and navigates', async () => {
    const mockToken = 'test-token-456';
    (axios.post as jest.Mock).mockResolvedValue({
      data: { token: mockToken }
    });

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getAllByRole('button', { name: /create account/i })[0]);

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInputs = screen.getAllByPlaceholderText(/password/i);
    const passwordInput = passwordInputs[0];
    const confirmPasswordInput = screen.getByPlaceholderText('Repeat your password');
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'newuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        'http://localhost:8080/auth/register',
        {
          username: 'newuser',
          password: 'password123'
        }
      );
      expect(localStorage.getItem('protube_token')).toBe(mockToken);
      expect(mockNavigate).toHaveBeenCalledWith('/', { replace: true });
    });
  });

  it('navigates to from location when provided', async () => {
    const mockToken = 'test-token-789';
    (axios.post as jest.Mock).mockResolvedValue({
      data: { token: mockToken }
    });
    mockUseLocation.mockReturnValue({
      pathname: '/auth',
      search: '',
      state: { from: '/video/123' }
    });

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getAllByPlaceholderText(/password/i)[0];
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/video/123', { replace: true });
    });
  });

  it('shows error for 401 status (incorrect credentials)', async () => {
    const error = {
      isAxiosError: true,
      response: { status: 401 }
    };
    (axios.isAxiosError as jest.Mock).mockReturnValue(true);
    (axios.post as jest.Mock).mockRejectedValue(error);

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getByPlaceholderText('Enter your password');
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Incorrect username or password.')).toBeInTheDocument();
    });
  });

  it('shows error for 409 status (username already exists)', async () => {
    const error = {
      isAxiosError: true,
      response: { status: 409 }
    };
    (axios.isAxiosError as jest.Mock).mockReturnValue(true);
    (axios.post as jest.Mock).mockRejectedValue(error);

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getAllByRole('button', { name: /create account/i })[0]);

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInputs = screen.getAllByPlaceholderText(/password/i);
    const passwordInput = passwordInputs[0];
    const confirmPasswordInput = screen.getByPlaceholderText('Repeat your password');
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'existinguser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('An account with this username already exists.')).toBeInTheDocument();
    });
  });

  it('shows generic error for other axios errors', async () => {
    const error = {
      isAxiosError: true,
      response: { status: 500 }
    };
    (axios.isAxiosError as jest.Mock).mockReturnValue(true);
    (axios.post as jest.Mock).mockRejectedValue(error);

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getAllByPlaceholderText(/password/i)[0];
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Could not contact the server. Please try again.')).toBeInTheDocument();
    });
  });

  it('shows generic error for non-axios errors', async () => {
    (axios.isAxiosError as jest.Mock).mockReturnValue(false);
    (axios.post as jest.Mock).mockRejectedValue(new Error('Network error'));

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getAllByPlaceholderText(/password/i)[0];
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Something went wrong. Please try again.')).toBeInTheDocument();
    });
  });

  it('disables submit button while submitting', async () => {
    let resolvePromise: (value: { data: { token: string } }) => void;
    const promise = new Promise<{ data: { token: string } }>((resolve) => {
      resolvePromise = resolve;
    });
    (axios.post as jest.Mock).mockReturnValue(promise);

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getAllByPlaceholderText(/password/i)[0];
    const form = usernameInput.closest('form')!;
    const submitButton = form.querySelector('button[type="submit"]') as HTMLButtonElement;

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(submitButton).toBeDisabled();
      expect(screen.getByText('Signing in...')).toBeInTheDocument();
    });

    resolvePromise!({ data: { token: 'token' } });
  });

  it('shows correct button text for sign up mode', async () => {
    let resolvePromise: (value: { data: { token: string } }) => void;
    const promise = new Promise<{ data: { token: string } }>((resolve) => {
      resolvePromise = resolve;
    });
    (axios.post as jest.Mock).mockReturnValue(promise);

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getAllByRole('button', { name: /create account/i })[0]);

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInputs = screen.getAllByPlaceholderText(/password/i);
    const passwordInput = passwordInputs[0];
    const confirmPasswordInput = screen.getByPlaceholderText('Repeat your password');
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: 'newuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText('Creating...')).toBeInTheDocument();
    });

    resolvePromise!({ data: { token: 'token' } });
  });

  it('trims username before submitting', async () => {
    const mockToken = 'test-token';
    (axios.post as jest.Mock).mockResolvedValue({
      data: { token: mockToken }
    });

    render(
      <BrowserRouter>
        <AuthPage />
      </BrowserRouter>
    );

    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getByPlaceholderText('Enter your password');
    const form = usernameInput.closest('form')!;

    fireEvent.change(usernameInput, { target: { value: '  testuser  ' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.submit(form);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        'http://localhost:8080/auth/login',
        {
          username: 'testuser',
          password: 'password123'
        }
      );
    });
  });
});

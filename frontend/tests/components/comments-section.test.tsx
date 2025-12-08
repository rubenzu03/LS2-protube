import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { CommentsSection } from '@/components/video/comments-section';
import { useVideoComments, useCreateComment } from '@/hooks/comment-hooks';

const mockNavigate = jest.fn();

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useNavigate: () => mockNavigate
}));

jest.mock('@/hooks/comment-hooks', () => ({
  useVideoComments: jest.fn(() => ({
    comments: [
      { id: 1, content: 'Great video!', username: 'user1', userId: 1 },
      { id: 2, content: 'Thanks for sharing', username: 'user2', userId: 2 }
    ],
    loading: 'success'
  })),
  useCreateComment: jest.fn(() => ({
    mutateAsync: jest.fn(),
    isPending: false
  }))
}));

// Helper to create a valid JWT token for testing
function createTestToken(username: string, expiresIn = 3600): string {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = btoa(JSON.stringify({
    sub: username,
    exp: Math.floor(Date.now() / 1000) + expiresIn
  }));
  const signature = 'test-signature';
  return `${header}.${payload}.${signature}`;
}

describe('CommentsSection', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  it('renders comments section with title', () => {
    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('Comments')).toBeInTheDocument();
  });

  it('displays comment count', () => {
    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('2 comments')).toBeInTheDocument();
  });

  it('renders all comments', () => {
    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('Great video!')).toBeInTheDocument();
    expect(screen.getByText('Thanks for sharing')).toBeInTheDocument();
  });

  it('shows sign in prompt when not authenticated', () => {
    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('Sign in to join the conversation.')).toBeInTheDocument();
  });

  it('shows comment input when authenticated', () => {
    const testToken = createTestToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByPlaceholderText('Add a public comment...')).toBeInTheDocument();
  });

  it('navigates to auth when sign in button clicked', () => {
    render(
      <BrowserRouter>
        <CommentsSection videoId="123" />
      </BrowserRouter>
    );

    const signInButton = screen.getByRole('button', { name: /sign in/i });
    fireEvent.click(signInButton);

    expect(mockNavigate).toHaveBeenCalledWith('/auth', { state: { from: '/video/123' } });
  });

  it('enables submit button when comment has content', () => {
    const testToken = createTestToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    const textarea = screen.getByPlaceholderText('Add a public comment...');
    const commentButton = screen.getByRole('button', { name: /^comment$/i });

    expect(commentButton).toBeDisabled();

    fireEvent.change(textarea, { target: { value: 'Nice video!' } });

    expect(commentButton).not.toBeDisabled();
  });

  it('clears content when cancel button clicked', () => {
    const testToken = createTestToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    const textarea = screen.getByPlaceholderText('Add a public comment...') as HTMLTextAreaElement;
    fireEvent.change(textarea, { target: { value: 'Test comment' } });

    expect(textarea.value).toBe('Test comment');

    const cancelButton = screen.getByRole('button', { name: /cancel/i });
    fireEvent.click(cancelButton);

    expect(textarea.value).toBe('');
  });

  it('calls mutateAsync when comment is submitted', async () => {
    const mockMutateAsync = jest.fn().mockResolvedValue({});
    useCreateComment.mockReturnValue({
      mutateAsync: mockMutateAsync,
      isPending: false
    });

    const testToken = createTestToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <CommentsSection videoId="5" />
      </BrowserRouter>
    );

    const textarea = screen.getByPlaceholderText('Add a public comment...');
    fireEvent.change(textarea, { target: { value: 'Great content!' } });

    const commentButton = screen.getByRole('button', { name: /^comment$/i });
    fireEvent.click(commentButton);

    await waitFor(() => {
      expect(mockMutateAsync).toHaveBeenCalledWith({
        content: 'Great content!',
        videoId: 5
      });
    });
  });

  it('shows loading state', () => {
    useVideoComments.mockReturnValue({
      comments: [],
      loading: 'loading'
    });

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('Loading comments...')).toBeInTheDocument();
  });

  it('shows error state', () => {
    useVideoComments.mockReturnValue({
      comments: [],
      loading: 'error'
    });

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('Failed to load comments.')).toBeInTheDocument();
  });

  it('shows empty state when no comments', () => {
    useVideoComments.mockReturnValue({
      comments: [],
      loading: 'success'
    });

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('No comments yet. Be the first to comment!')).toBeInTheDocument();
  });

  it('handles storage event for token changes', () => {
    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('Sign in to join the conversation.')).toBeInTheDocument();

    const testToken = createTestToken('newuser');
    const storageEvent = new StorageEvent('storage', {
      key: 'protube_token',
      newValue: testToken
    });
    window.dispatchEvent(storageEvent);

    waitFor(() => {
      expect(screen.getByPlaceholderText('Add a public comment...')).toBeInTheDocument();
    });
  });

  it('displays fallback username for comments without username', () => {
    useVideoComments.mockReturnValue({
      comments: [
        { id: 1, content: 'Comment 1', username: '', userId: 42 },
        { id: 2, content: 'Comment 2', username: null, userId: 99 }
      ],
      loading: 'success'
    });

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    expect(screen.getByText('User #42')).toBeInTheDocument();
    expect(screen.getByText('User #99')).toBeInTheDocument();
  });

  it('does not submit when content is empty or whitespace', () => {
    const mockMutateAsync = jest.fn();
    useCreateComment.mockReturnValue({
      mutateAsync: mockMutateAsync,
      isPending: false
    });

    const testToken = createTestToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    const textarea = screen.getByPlaceholderText('Add a public comment...');
    fireEvent.change(textarea, { target: { value: '   ' } });

    const commentButton = screen.getByRole('button', { name: /^comment$/i });
    expect(commentButton).toBeDisabled();
  });

  it('does not submit when videoId is missing', async () => {
    const mockMutateAsync = jest.fn();
    useCreateComment.mockReturnValue({
      mutateAsync: mockMutateAsync,
      isPending: false
    });

    const testToken = createTestToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <CommentsSection />
      </BrowserRouter>
    );

    const textarea = screen.getByPlaceholderText('Add a public comment...');
    fireEvent.change(textarea, { target: { value: 'Test' } });

    const commentButton = screen.getByRole('button', { name: /^comment$/i });
    fireEvent.click(commentButton);

    await waitFor(() => {
      expect(mockMutateAsync).not.toHaveBeenCalled();
    });
  });

  it('clears textarea after successful comment submission', async () => {
    const mockMutateAsync = jest.fn().mockResolvedValue({});
    useCreateComment.mockReturnValue({
      mutateAsync: mockMutateAsync,
      isPending: false
    });

    const testToken = createTestToken('testuser');
    localStorage.setItem('protube_token', testToken);

    render(
      <BrowserRouter>
        <CommentsSection videoId="1" />
      </BrowserRouter>
    );

    const textarea = screen.getByPlaceholderText('Add a public comment...') as HTMLTextAreaElement;
    fireEvent.change(textarea, { target: { value: 'My comment' } });

    const commentButton = screen.getByRole('button', { name: /^comment$/i });
    fireEvent.click(commentButton);

    await waitFor(() => {
      expect(textarea.value).toBe('');
    });
  });
});

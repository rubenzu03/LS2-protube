import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ChannelPage } from '@/components/pages/channel-page';
import * as videoHooks from '@/hooks/video-hooks';

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useParams: () => ({ id: 'user123' })
}));

jest.mock('@/hooks/video-hooks');

const mockUser = {
  id: 'user123',
  username: 'TestUser'
};

const mockVideos = [
  {
    id: '1',
    title: 'Test Video 1',
    description: 'Description 1',
    uploader: 'TestUser',
    uploaderId: 'user123',
    uploadDate: '2024-01-01',
    views: 100,
    likes: 10,
    isLiked: false
  }
];

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false }
    }
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{children}</BrowserRouter>
    </QueryClientProvider>
  );
};

describe('ChannelPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('shows loading state', () => {
    (videoHooks.useChannelData as jest.Mock).mockReturnValue({
      user: null,
      videos: [],
      thumbnails: [],
      loading: 'loading',
      message: ''
    });

    render(<ChannelPage />, { wrapper: createWrapper() });

    expect(screen.getByText('Loading channel...')).toBeInTheDocument();
  });

  it('shows error state', () => {
    (videoHooks.useChannelData as jest.Mock).mockReturnValue({
      user: null,
      videos: [],
      thumbnails: [],
      loading: 'error',
      message: 'Failed to load channel'
    });

    render(<ChannelPage />, { wrapper: createWrapper() });

    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
    expect(screen.getByText('Failed to load channel')).toBeInTheDocument();
  });

  it('shows not found message when user is null', () => {
    (videoHooks.useChannelData as jest.Mock).mockReturnValue({
      user: null,
      videos: [],
      thumbnails: [],
      loading: 'loaded',
      message: ''
    });

    render(<ChannelPage />, { wrapper: createWrapper() });

    expect(screen.getByText('Channel not found')).toBeInTheDocument();
  });

  it('renders channel with videos', async () => {
    (videoHooks.useChannelData as jest.Mock).mockReturnValue({
      user: mockUser,
      videos: mockVideos,
      thumbnails: [{ id: '1', filename: 'thumb1.jpg' }],
      loading: 'loaded',
      message: ''
    });

    render(<ChannelPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText('TestUser')).toBeInTheDocument();
      expect(screen.getByText('Test Video 1')).toBeInTheDocument();
      expect(screen.getByText('1 video')).toBeInTheDocument();
    });
  });

  it('shows empty state when channel has no videos', () => {
    (videoHooks.useChannelData as jest.Mock).mockReturnValue({
      user: mockUser,
      videos: [],
      thumbnails: [],
      loading: 'loaded',
      message: ''
    });

    render(<ChannelPage />, { wrapper: createWrapper() });

    expect(screen.getByText("This channel hasn't uploaded any videos yet.")).toBeInTheDocument();
  });

  it('shows correct plural for videos count', () => {
    (videoHooks.useChannelData as jest.Mock).mockReturnValue({
      user: mockUser,
      videos: [mockVideos[0], { ...mockVideos[0], id: '2' }],
      thumbnails: [],
      loading: 'loaded',
      message: ''
    });

    render(<ChannelPage />, { wrapper: createWrapper() });

    expect(screen.getByText('2 videos')).toBeInTheDocument();
  });
});

import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router';
import { Home } from '@/components/pages/home';
import { useAllVideos } from '@/hooks/video-hooks';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

jest.mock('@/hooks/video-hooks');
jest.mock('@/hooks/use-document-title');
jest.mock('@/utils/api');

const mockVideos: Video[] = [
  {
    id: '1',
    title: 'Test Video 1',
    description: 'Description 1',
    duration: 120,
    width: 1920,
    height: 1080,
    filename: 'video1.mp4',
    userId: 'user1',
    categoryId: 'cat1',
    tagId: 'tag1',
    commentId: 'comment1',
  },
  {
    id: '2',
    title: 'Test Video 2',
    description: 'Description 2',
    duration: 180,
    width: 1920,
    height: 1080,
    filename: 'video2.mp4',
    userId: 'user2',
    categoryId: 'cat2',
    tagId: 'tag2',
    commentId: 'comment2',
  },
];

const mockThumbnails: Thumbnail[] = [
  { id: '1', filename: 'thumb1.jpg' },
  { id: '2', filename: 'thumb2.jpg' },
];

const createWrapper = () => {
  const queryClient = new QueryClient();
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{children}</BrowserRouter>
    </QueryClientProvider>
  );
};

describe('Home Page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('displays loading state', () => {
    (useAllVideos as jest.Mock).mockReturnValue({
      loading: 'loading',
      videos: [],
      thumbnails: [],
      message: 'Loading...',
    });

    render(<Home />, { wrapper: createWrapper() });

    expect(screen.getByText('Loading videos...')).toBeInTheDocument();
  });

  it('displays error state', () => {
    const errorMessage = 'Failed to fetch videos';
    (useAllVideos as jest.Mock).mockReturnValue({
      loading: 'error',
      videos: [],
      thumbnails: [],
      message: errorMessage,
    });

    render(<Home />, { wrapper: createWrapper() });

    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });

  it('displays videos when loaded successfully', () => {
    (useAllVideos as jest.Mock).mockReturnValue({
      loading: 'success',
      videos: mockVideos,
      thumbnails: mockThumbnails,
      message: '',
    });

    render(<Home />, { wrapper: createWrapper() });

    expect(screen.getByText('Test Video 1')).toBeInTheDocument();
    expect(screen.getByText('Test Video 2')).toBeInTheDocument();
  });

  it('renders correct number of video cards', () => {
    (useAllVideos as jest.Mock).mockReturnValue({
      loading: 'success',
      videos: mockVideos,
      thumbnails: mockThumbnails,
      message: '',
    });

    render(<Home />, { wrapper: createWrapper() });

    const videoTitles = screen.getAllByText(/Test Video/);
    expect(videoTitles).toHaveLength(2);
  });

  it('renders empty state when no videos', () => {
    (useAllVideos as jest.Mock).mockReturnValue({
      loading: 'success',
      videos: [],
      thumbnails: [],
      message: '',
    });

    const { container } = render(<Home />, { wrapper: createWrapper() });

    const videoCards = container.querySelectorAll('[class*="VideoCard"]');
    expect(videoCards).toHaveLength(0);
  });
});


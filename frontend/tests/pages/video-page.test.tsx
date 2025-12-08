import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { VideoPage } from '@/components/pages/video-page';
import * as api from '@/utils/api';
import * as videoHooks from '@/hooks/video-hooks';

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useParams: () => ({ id: '1' })
}));

jest.mock('@/utils/api');
jest.mock('@/hooks/video-hooks', () => ({
  useAllVideos: jest.fn(),
  useUserInfo: jest.fn(() => ({
    user: { id: 'user123', username: 'TestUser' },
    isLoading: false,
    isError: false
  }))
}));
jest.mock('@/hooks/use-document-title');

const mockVideo = {
  id: '1',
  title: 'Test Video',
  description: 'Test description',
  uploader: 'testuser',
  uploaderId: 'user123',
  uploadDate: '2024-01-01',
  views: 1000,
  likes: 50,
  isLiked: false
};

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

describe('VideoPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (api.getVideoPageData as jest.Mock).mockResolvedValue({
      video: mockVideo,
      thumbnail: 'http://localhost:8080/api/videos/thumbnail/1'
    });
    (api.getVideoStreamUrl as jest.Mock).mockReturnValue('http://localhost:8080/api/videos/stream/1');
    (videoHooks.useAllVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: false
    });
  });

  it('renders video player when data loads', async () => {
    render(<VideoPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText('Test Video')).toBeInTheDocument();
    });
  });

  it('shows loading state', () => {
    (api.getVideoPageData as jest.Mock).mockImplementation(
      () => new Promise(() => { }) // Never resolves
    );

    render(<VideoPage />, { wrapper: createWrapper() });

    expect(screen.queryByText('Test Video')).not.toBeInTheDocument();
  });

  it('calls getVideoPageData with correct id', async () => {
    render(<VideoPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(api.getVideoPageData).toHaveBeenCalledWith('1');
    });
  });
});

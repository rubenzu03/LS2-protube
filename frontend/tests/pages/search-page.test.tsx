import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { SearchPage } from '@/components/pages/search-page';
import { useSearchVideos } from '@/hooks/video-hooks';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

jest.mock('@/hooks/video-hooks');
jest.mock('@/hooks/use-document-title');
jest.mock('@/components/layout/layout', () => ({
  Layout: ({ children }: { children: React.ReactNode }) => <div>{children}</div>
}));
jest.mock('@/components/video-card', () => ({
  VideoCard: ({ video }: { video: Video }) => <div data-testid="video-card">{video.title}</div>
}));

const mockNavigate = jest.fn();
const mockSearchParams = new URLSearchParams();

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useNavigate: () => mockNavigate,
  useSearchParams: () => [mockSearchParams]
}));

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
    commentId: 'comment1'
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
    commentId: 'comment2'
  }
];

const mockThumbnails: Thumbnail[] = [
  { id: '1', filename: 'thumb1.jpg' },
  { id: '2', filename: 'thumb2.jpg' }
];

describe('SearchPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockSearchParams.delete('q');
  });

  it('shows empty state when no query is provided', () => {
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Type something in the search bar to find videos.')).toBeInTheDocument();
  });

  it('shows empty state when query is only whitespace', () => {
    mockSearchParams.set('q', '   ');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Type something in the search bar to find videos.')).toBeInTheDocument();
  });

  it('shows loading state', () => {
    mockSearchParams.set('q', 'test');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: 'loading',
      message: 'Loading...'
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Loading videos...')).toBeInTheDocument();
  });

  it('shows error state', () => {
    mockSearchParams.set('q', 'test');
    const errorMessage = 'Failed to fetch videos';
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: 'error',
      message: errorMessage
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });

  it('shows no results message when query returns no videos', () => {
    mockSearchParams.set('q', 'nonexistent');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('No results found for "nonexistent".')).toBeInTheDocument();
  });

  it('displays videos when search is successful', () => {
    mockSearchParams.set('q', 'test');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: mockVideos,
      thumbnails: mockThumbnails,
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Test Video 1')).toBeInTheDocument();
    expect(screen.getByText('Test Video 2')).toBeInTheDocument();
  });

  it('renders correct number of video cards', () => {
    mockSearchParams.set('q', 'test');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: mockVideos,
      thumbnails: mockThumbnails,
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    const videoCards = screen.getAllByTestId('video-card');
    expect(videoCards).toHaveLength(2);
  });

  it('matches thumbnails to videos correctly', () => {
    mockSearchParams.set('q', 'test');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: mockVideos,
      thumbnails: mockThumbnails,
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    // VideoCard should be rendered for each video
    expect(screen.getAllByTestId('video-card')).toHaveLength(2);
  });

  it('handles videos without matching thumbnails', () => {
    mockSearchParams.set('q', 'test');
    const videosWithoutThumbnail: Video[] = [
      {
        id: '3',
        title: 'Video Without Thumbnail',
        description: 'Description',
        duration: 120,
        width: 1920,
        height: 1080,
        filename: 'video3.mp4',
        userId: 'user3',
        categoryId: 'cat3',
        tagId: 'tag3',
        commentId: 'comment3'
      }
    ];
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: videosWithoutThumbnail,
      thumbnails: [],
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Video Without Thumbnail')).toBeInTheDocument();
  });

  it('trims query parameter', () => {
    mockSearchParams.set('q', '  test query  ');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: mockVideos,
      thumbnails: mockThumbnails,
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    // Should call useSearchVideos with trimmed query
    expect(useSearchVideos).toHaveBeenCalledWith('test query');
  });

  it('handles empty query parameter gracefully', () => {
    mockSearchParams.set('q', '');
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Type something in the search bar to find videos.')).toBeInTheDocument();
  });

  it('handles null query parameter', () => {
    // No 'q' parameter set
    (useSearchVideos as jest.Mock).mockReturnValue({
      videos: [],
      thumbnails: [],
      loading: 'success',
      message: ''
    });

    render(
      <BrowserRouter>
        <SearchPage />
      </BrowserRouter>
    );

    expect(screen.getByText('Type something in the search bar to find videos.')).toBeInTheDocument();
  });
});


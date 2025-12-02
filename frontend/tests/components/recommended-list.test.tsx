import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { RecommendedList } from '@/components/video/recommended-list';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

const mockVideos: Video[] = [
  {
    id: '1',
    title: 'Video 1',
    description: 'Description 1',
    uploader: 'user1',
    uploaderId: 'user1',
    uploadDate: '2024-01-01',
    views: 100,
    likes: 10,
    isLiked: false,
    duration: 125 // 2:05
  },
  {
    id: '2',
    title: 'Video 2',
    description: 'Description 2',
    uploader: 'user2',
    uploaderId: 'user2',
    uploadDate: '2024-01-02',
    views: 200,
    likes: 20,
    isLiked: false,
    duration: 65 // 1:05
  },
  {
    id: '3',
    title: 'Video 3',
    description: 'Description 3',
    uploader: 'user3',
    uploaderId: 'user3',
    uploadDate: '2024-01-03',
    views: 300,
    likes: 30,
    isLiked: false,
    duration: 600 // 10:00
  }
];

const mockThumbnails: Thumbnail[] = [
  { id: '1', filename: 'thumb1.jpg' },
  { id: '2', filename: 'thumb2.jpg' },
  { id: '3', filename: 'thumb3.jpg' }
];

describe('RecommendedList', () => {
  it('renders list of videos', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={mockVideos} thumbnails={mockThumbnails} />
      </BrowserRouter>
    );

    expect(screen.getByText('Video 1')).toBeInTheDocument();
    expect(screen.getByText('Video 2')).toBeInTheDocument();
    expect(screen.getByText('Video 3')).toBeInTheDocument();
  });

  it('excludes current video from list', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={mockVideos} thumbnails={mockThumbnails} currentId="1" />
      </BrowserRouter>
    );

    expect(screen.queryByText('Video 1')).not.toBeInTheDocument();
    expect(screen.getByText('Video 2')).toBeInTheDocument();
    expect(screen.getByText('Video 3')).toBeInTheDocument();
  });

  it('respects limit prop', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={mockVideos} thumbnails={mockThumbnails} limit={1} />
      </BrowserRouter>
    );

    expect(screen.getByText('Video 1')).toBeInTheDocument();
    expect(screen.queryByText('Video 2')).not.toBeInTheDocument();
    expect(screen.queryByText('Video 3')).not.toBeInTheDocument();
  });

  it('formats duration correctly', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={mockVideos} thumbnails={mockThumbnails} />
      </BrowserRouter>
    );

    expect(screen.getByText('2:05')).toBeInTheDocument();
    expect(screen.getByText('1:05')).toBeInTheDocument();
    expect(screen.getByText('10:00')).toBeInTheDocument();
  });

  it('renders video thumbnails', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={mockVideos} thumbnails={mockThumbnails} />
      </BrowserRouter>
    );

    const images = screen.getAllByRole('img');
    expect(images.length).toBeGreaterThan(0);
  });

  it('renders video descriptions', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={mockVideos} thumbnails={mockThumbnails} />
      </BrowserRouter>
    );

    expect(screen.getByText('Description 1')).toBeInTheDocument();
    expect(screen.getByText('Description 2')).toBeInTheDocument();
  });

  it('creates links to video pages', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={mockVideos} thumbnails={mockThumbnails} />
      </BrowserRouter>
    );

    const links = screen.getAllByRole('link');
    expect(links[0]).toHaveAttribute('href', '/video/1');
    expect(links[1]).toHaveAttribute('href', '/video/2');
  });

  it('handles empty videos array', () => {
    render(
      <BrowserRouter>
        <RecommendedList videos={[]} thumbnails={[]} />
      </BrowserRouter>
    );

    expect(screen.queryByRole('link')).not.toBeInTheDocument();
  });

  it('uses default limit of 8', () => {
    const manyVideos = Array.from({ length: 10 }, (_, i) => ({
      ...mockVideos[0],
      id: String(i + 1),
      title: `Video ${i + 1}`
    }));

    render(
      <BrowserRouter>
        <RecommendedList videos={manyVideos} thumbnails={mockThumbnails} />
      </BrowserRouter>
    );

    const links = screen.getAllByRole('link');
    expect(links).toHaveLength(8);
  });
});

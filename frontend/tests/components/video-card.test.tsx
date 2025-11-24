import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { VideoCard } from '@/components/video-card';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

jest.mock('@/utils/api');

const mockVideo: Video = {
  id: '1',
  title: 'Test Video Title',
  description: 'Test description',
  duration: 125, // 2:05
  width: 1920,
  height: 1080,
  filename: 'test.mp4',
  userId: 'user123',
  categoryId: 'cat1',
  tagId: 'tag1',
  commentId: 'comment1'
};

const mockThumbnail: Thumbnail = {
  id: '1',
  filename: 'thumbnail.jpg'
};

describe('VideoCard', () => {
  it('renders video title', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    expect(screen.getByText('Test Video Title')).toBeInTheDocument();
  });

  it('displays correct video duration', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    expect(screen.getByText('2:05')).toBeInTheDocument();
  });

  it('formats duration with leading zero for seconds', () => {
    const videoWithShortSeconds = { ...mockVideo, duration: 65 }; // 1:05
    const { rerender } = render(
      <BrowserRouter>
        <VideoCard video={videoWithShortSeconds} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    expect(screen.getByText('1:05')).toBeInTheDocument();

    const videoWith10Seconds = { ...mockVideo, duration: 610 }; // 10:10
    rerender(
      <BrowserRouter>
        <VideoCard video={videoWith10Seconds} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    expect(screen.getByText('10:10')).toBeInTheDocument();
  });

  it('displays channel label', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    expect(screen.getByText('user123')).toBeInTheDocument();
  });

  it('renders link to video page', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const links = screen.getAllByRole('link');
    const videoLink = links.find(link => link.getAttribute('href') === '/video/1');

    expect(videoLink).toBeInTheDocument();
  });

  it('renders links to channel page', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const channelLinks = screen.getAllByRole('link').filter(
      link => link.getAttribute('href') === '/channel/user123'
    );

    expect(channelLinks.length).toBeGreaterThan(0);
  });

  it('renders thumbnail image', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const img = screen.getByAltText('Test Video Title');
    expect(img).toBeInTheDocument();
  });
});

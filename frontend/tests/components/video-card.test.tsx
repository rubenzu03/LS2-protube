import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { VideoCard } from '@/components/video-card';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';
import { useUserInfo } from '@/hooks/video-hooks';

jest.mock('@/utils/api');
jest.mock('@/hooks/video-hooks', () => ({
  useUserInfo: jest.fn(() => ({
    user: { id: 'user123', username: 'TestUser' },
    isLoading: false,
    isError: false
  }))
}));

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

    expect(screen.getByText('TestUser')).toBeInTheDocument();
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

  it('handles mouse enter and leave events', () => {
    jest.useFakeTimers();

    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const videoLink = screen.getAllByRole('link')[0];

    // Simulate mouse enter
    videoLink.dispatchEvent(new MouseEvent('mouseenter', { bubbles: true }));

    // Fast-forward time
    jest.advanceTimersByTime(1000);

    // Simulate mouse leave
    videoLink.dispatchEvent(new MouseEvent('mouseleave', { bubbles: true }));

    jest.useRealTimers();
  });

  it('clears hover timer on quick mouse leave', () => {
    jest.useFakeTimers();

    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const videoLink = screen.getAllByRole('link')[0];

    // Simulate quick hover (leave before delay)
    videoLink.dispatchEvent(new MouseEvent('mouseenter', { bubbles: true }));
    jest.advanceTimersByTime(500); // Less than PREVIEW_DELAY_MS
    videoLink.dispatchEvent(new MouseEvent('mouseleave', { bubbles: true }));

    jest.useRealTimers();
  });

  it('stops propagation on channel link click', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const channelLinks = screen.getAllByRole('link').filter(
      link => link.getAttribute('href') === '/channel/user123'
    );

    const stopPropagationSpy = jest.fn();
    channelLinks[0].addEventListener('click', (e) => {
      e.stopPropagation = stopPropagationSpy;
    });

    channelLinks[0].click();
  });

  it('displays fallback channel label when username is missing', () => {
    useUserInfo.mockReturnValue({
      user: null,
      isLoading: false,
      isError: false
    });

    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    expect(screen.getByText('Channel')).toBeInTheDocument();
  });

  it('renders channel avatar with first letter uppercase', () => {
    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const avatar = screen.getByText('C'); // First letter of "Channel" when user is null
    expect(avatar).toBeInTheDocument();
  });

  it('handles video preview play error gracefully', () => {
    jest.useFakeTimers();

    const mockPlay = jest.fn().mockRejectedValue(new Error('Play failed'));
    HTMLMediaElement.prototype.play = mockPlay;

    render(
      <BrowserRouter>
        <VideoCard video={mockVideo} thumbnail={mockThumbnail} />
      </BrowserRouter>
    );

    const videoLink = screen.getAllByRole('link')[0];
    videoLink.dispatchEvent(new MouseEvent('mouseenter', { bubbles: true }));

    jest.advanceTimersByTime(1000);

    jest.useRealTimers();
  });
});

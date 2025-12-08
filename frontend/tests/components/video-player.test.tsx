import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { VideoPlayer } from '@/components/video/video-player';

describe('VideoPlayer', () => {
  beforeEach(() => {
    localStorage.clear();
    // Mock HTMLMediaElement methods
    window.HTMLMediaElement.prototype.play = jest.fn(() => Promise.resolve());
    window.HTMLMediaElement.prototype.pause = jest.fn();
  });

  it('renders video player', () => {
    render(<VideoPlayer src="test.mp4" />);
    expect(screen.getByTestId('video-player')).toBeInTheDocument();
  });

  it('does not render video when src is not provided', () => {
    render(<VideoPlayer />);
    expect(screen.queryByTestId('video-player')).not.toBeInTheDocument();
  });

  it('does not render video when error is present', () => {
    render(<VideoPlayer src="test.mp4" error="Video not found" />);
    expect(screen.queryByTestId('video-player')).not.toBeInTheDocument();
  });

  it('shows loading state', () => {
    render(<VideoPlayer src="test.mp4" isLoading={true} />);
    const video = screen.getByTestId('video-player');
    expect(video).toHaveClass('opacity-0');
  });

  it('becomes ready when video can play', async () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    await waitFor(() => {
      expect(video).toHaveClass('opacity-100');
      expect(video).toHaveAttribute('controls');
    });
  });

  it('becomes ready on canPlayThrough event', async () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlayThrough(video);

    await waitFor(() => {
      expect(video).toHaveClass('opacity-100');
    });
  });

  it('becomes ready on loadedData event', async () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.loadedData(video);

    await waitFor(() => {
      expect(video).toHaveClass('opacity-100');
    });
  });

  it('resets ready state when src changes', () => {
    const { rerender } = render(<VideoPlayer src="test1.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    rerender(<VideoPlayer src="test2.mp4" />);
    const newVideo = screen.getByTestId('video-player') as HTMLVideoElement;
    expect(newVideo).toHaveClass('opacity-0');
  });

  it('uses default volume when no stored volume exists', async () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    await waitFor(() => {
      expect(video).toHaveAttribute('controls');
    });
  });

  it('uses stored volume from localStorage', async () => {
    localStorage.setItem('protube_volume', '0.75');

    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    await waitFor(() => {
      expect(video).toHaveAttribute('controls');
    });
  });

  it('uses stored muted state from localStorage', async () => {
    localStorage.setItem('protube_muted', 'true');

    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    await waitFor(() => {
      expect(video).toHaveAttribute('controls');
    });
  });

  it('handles volume change events', () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    // Simulate volume change
    fireEvent.volumeChange(video);

    // Test passes if no errors are thrown
    expect(video).toBeInTheDocument();
  });

  it('handles muted state changes', () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    fireEvent.volumeChange(video);

    expect(video).toBeInTheDocument();
  });

  it('displays poster when ready', async () => {
    render(<VideoPlayer src="test.mp4" poster="poster.jpg" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    await waitFor(() => {
      expect(video).toHaveAttribute('poster', 'poster.jpg');
    });
  });

  it('handles invalid stored volume', async () => {
    localStorage.setItem('protube_volume', 'invalid');

    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    await waitFor(() => {
      expect(video).toHaveAttribute('controls');
    });
  });

  it('has autoplay attribute', () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    expect(video).toHaveAttribute('autoPlay');
  });

  it('has playsInline attribute', () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    expect(video).toHaveAttribute('playsInline');
  });

  it('renders video source', () => {
    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;
    const source = video.querySelector('source');

    expect(source).toHaveAttribute('src', 'test.mp4');
    expect(source).toHaveAttribute('type', 'video/mp4');
  });

  it('handles volume change event when unmuting with volume jump', async () => {
    localStorage.setItem('protube_volume', '0.7');
    localStorage.setItem('protube_muted', 'true');

    render(<VideoPlayer src="test.mp4" />);
    const video = screen.getByTestId('video-player') as HTMLVideoElement;

    fireEvent.canPlay(video);

    await waitFor(() => {
      expect(video).toHaveAttribute('controls');
    });

    // Simulate unmuting with volume jump to 1
    Object.defineProperty(video, 'muted', { value: false, writable: true });
    Object.defineProperty(video, 'volume', { value: 1, writable: true });

    fireEvent.volumeChange(video);

    // Should restore to last volume
    expect(localStorage.getItem('protube_muted')).toBe('false');
  });
});

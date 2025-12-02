import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { VideoDescription } from '@/components/video/video-description';

describe('VideoDescription', () => {
  it('renders video description', () => {
    render(
      <BrowserRouter>
        <VideoDescription description="This is a test video description" />
      </BrowserRouter>
    );

    expect(screen.getByText('This is a test video description')).toBeInTheDocument();
  });

  it('renders with empty description', () => {
    render(
      <BrowserRouter>
        <VideoDescription description="" />
      </BrowserRouter>
    );

    expect(screen.queryByText('This is a test video description')).not.toBeInTheDocument();
  });
});

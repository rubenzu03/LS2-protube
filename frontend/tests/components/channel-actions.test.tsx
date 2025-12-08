import { render, screen } from '@testing-library/react';
import { ChannelActions } from '@/components/video/channel-actions';
import { BrowserRouter } from 'react-router';
import { useUserInfo } from '@/hooks/video-hooks';

jest.mock('@/hooks/video-hooks', () => ({
  useUserInfo: jest.fn(() => ({
    user: { id: 'user123', username: 'TestUser' },
    isLoading: false,
    isError: false
  }))
}));

describe('ChannelActions', () => {
  it('renders channel actions component', () => {
    render(
      <BrowserRouter>
        <ChannelActions />
      </BrowserRouter>
    );

    expect(screen.getByText('TestUser')).toBeInTheDocument();
  });

  it('displays uploader username when provided', () => {
    render(
      <BrowserRouter>
        <ChannelActions uploaderId="user123" />
      </BrowserRouter>
    );

    expect(screen.getByText('TestUser')).toBeInTheDocument();
    expect(screen.getByText('@testuser')).toBeInTheDocument();
  });

  it('displays "Unknown" when uploader id is not provided', () => {
    (useUserInfo as jest.Mock).mockReturnValueOnce({
      user: null,
      isLoading: false,
      isError: false
    });

    render(
      <BrowserRouter>
        <ChannelActions />
      </BrowserRouter>
    );

    expect(screen.getByText('Unknown')).toBeInTheDocument();
  });

  it('renders channel avatar placeholder with first letter', () => {
    render(
      <BrowserRouter>
        <ChannelActions uploaderId="test-id" />
      </BrowserRouter>
    );

    const avatarContainer = screen.getByText('T').closest('div');
    expect(avatarContainer).toBeInTheDocument();
    expect(avatarContainer).toHaveClass('rounded-full');
  });

  it('renders links to channel page', () => {
    render(
      <BrowserRouter>
        <ChannelActions uploaderId="user123" />
      </BrowserRouter>
    );

    const links = screen.getAllByRole('link');
    expect(links).toHaveLength(2);
    expect(links[0]).toHaveAttribute('href', '/channel/user123');
    expect(links[1]).toHaveAttribute('href', '/channel/user123');
  });
});

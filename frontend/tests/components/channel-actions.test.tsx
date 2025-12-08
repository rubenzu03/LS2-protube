import { render, screen } from '@testing-library/react';
import { ChannelActions } from '@/components/video/channel-actions';
import { BrowserRouter } from 'react-router';

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
    expect(screen.getByText('Subscribe')).toBeInTheDocument();
  });

  it('displays uploader id when provided', () => {
    render(
      <BrowserRouter>
        <ChannelActions uploaderId="user123" />
      </BrowserRouter>
    );

    expect(screen.getByText('TestUser')).toBeInTheDocument();
  });

  it('displays "Unknown" when uploader id is not provided', () => {
    const { useUserInfo } = require('@/hooks/video-hooks');
    useUserInfo.mockReturnValueOnce({
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

  it('renders like button', () => {
    render(
      <BrowserRouter>
        <ChannelActions />
      </BrowserRouter>
    );

    expect(screen.getByText('Like')).toBeInTheDocument();
  });

  it('renders subscribe button', () => {
    render(
      <BrowserRouter>
        <ChannelActions uploaderId="test-user" />
      </BrowserRouter>
    );

    const subscribeButton = screen.getByText('Subscribe');
    expect(subscribeButton).toBeInTheDocument();
  });

  it('renders channel avatar placeholder', () => {
    render(
      <BrowserRouter>
        <ChannelActions uploaderId="test-id" />
      </BrowserRouter>
    );

    // The component uses a div with gradient background, not an img tag
    const avatarContainer = screen.getByText('T').closest('div');
    expect(avatarContainer).toBeInTheDocument();
    expect(avatarContainer).toHaveClass('rounded-full');
  });
});

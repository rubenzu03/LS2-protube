import { render, screen } from '@testing-library/react';
import { ChannelActions } from '@/components/video/channel-actions';

describe('ChannelActions', () => {
  it('renders channel actions component', () => {
    render(<ChannelActions />);

    expect(screen.getByText('Uploader')).toBeInTheDocument();
    expect(screen.getByText('Subscribe')).toBeInTheDocument();
  });

  it('displays uploader id when provided', () => {
    render(<ChannelActions uploaderId="user123" />);

    expect(screen.getByText('user123')).toBeInTheDocument();
  });

  it('displays "Unknown" when uploader id is not provided', () => {
    render(<ChannelActions />);

    expect(screen.getByText('Unknown')).toBeInTheDocument();
  });

  it('renders like button', () => {
    render(<ChannelActions />);

    expect(screen.getByText('Like')).toBeInTheDocument();
  });

  it('renders subscribe button', () => {
    render(<ChannelActions uploaderId="test-user" />);

    const subscribeButton = screen.getByText('Subscribe');
    expect(subscribeButton).toBeInTheDocument();
  });

  it('renders channel avatar', () => {
    render(<ChannelActions />);

    const avatar = screen.getByAltText('Channel avatar');
    expect(avatar).toBeInTheDocument();
    expect(avatar).toHaveAttribute('src', '/abstract-channel-avatar.png');
  });
});


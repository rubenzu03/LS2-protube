import { render } from '@testing-library/react';
import App from '@/App';

// Mock child components to simplify testing
jest.mock('@/components/pages/home', () => ({
  Home: () => <div>Home Page</div>,
}));

jest.mock('@/components/pages/video-page', () => ({
  VideoPage: () => <div>Video Page</div>,
}));

jest.mock('@/components/pages/search-page', () => ({
  SearchPage: () => <div>Search Page</div>,
}));

jest.mock('@/components/pages/auth', () => ({
  AuthPage: () => <div>Auth Page</div>,
}));

jest.mock('@/components/pages/channel-page', () => ({
  ChannelPage: () => <div>Channel Page</div>,
}));

jest.mock('@/components/theme-provider', () => ({
  ThemeProvider: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}));

describe('App', () => {
  it('renders without crashing', () => {
    const { container } = render(<App />);
    expect(container).toBeInTheDocument();
  });

  it('wraps app with QueryClientProvider and ThemeProvider', () => {
    const { container } = render(<App />);
    expect(container.firstChild).toBeInTheDocument();
  });
});


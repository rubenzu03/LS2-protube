import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { Layout } from '@/components/layout/layout';

jest.mock('@/components/search-bar', () => ({
  SearchBar: () => <div>SearchBar</div>
}));

jest.mock('@/components/user-button', () => ({
  UserButton: () => <div>UserButton</div>
}));

describe('Layout', () => {
  it('renders children', () => {
    render(
      <BrowserRouter>
        <Layout>
          <div>Test Content</div>
        </Layout>
      </BrowserRouter>
    );

    expect(screen.getByText('Test Content')).toBeInTheDocument();
  });

  it('renders header with ProTube logo', () => {
    render(
      <BrowserRouter>
        <Layout>
          <div>Content</div>
        </Layout>
      </BrowserRouter>
    );

    const logo = screen.getByAltText('ProTube Logo');
    expect(logo).toBeInTheDocument();
  });

  it('renders SearchBar component', () => {
    render(
      <BrowserRouter>
        <Layout>
          <div>Content</div>
        </Layout>
      </BrowserRouter>
    );

    expect(screen.getByText('SearchBar')).toBeInTheDocument();
  });

  it('renders UserButton component', () => {
    render(
      <BrowserRouter>
        <Layout>
          <div>Content</div>
        </Layout>
      </BrowserRouter>
    );

    expect(screen.getByText('UserButton')).toBeInTheDocument();
  });

  it('handles scroll event to hide border', () => {
    render(
      <BrowserRouter>
        <Layout>
          <div>Content</div>
        </Layout>
      </BrowserRouter>
    );

    // Simulate scroll event
    Object.defineProperty(window, 'scrollY', { value: 200, writable: true });
    Object.defineProperty(document.documentElement, 'scrollHeight', { value: 2000, writable: true });
    Object.defineProperty(window, 'innerHeight', { value: 800, writable: true });

    const scrollEvent = new Event('scroll');
    window.dispatchEvent(scrollEvent);
  });

  it('shows border when scroll is less than 10%', () => {
    render(
      <BrowserRouter>
        <Layout>
          <div>Content</div>
        </Layout>
      </BrowserRouter>
    );

    // Simulate minimal scroll
    Object.defineProperty(window, 'scrollY', { value: 10, writable: true });
    Object.defineProperty(document.documentElement, 'scrollHeight', { value: 2000, writable: true });
    Object.defineProperty(window, 'innerHeight', { value: 800, writable: true });

    const scrollEvent = new Event('scroll');
    window.dispatchEvent(scrollEvent);
  });
});

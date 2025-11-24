import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router';
import { SearchBar } from '@/components/search-bar';

const mockNavigate = jest.fn();

jest.mock('react-router', () => ({
  ...jest.requireActual('react-router'),
  useNavigate: () => mockNavigate,
}));

describe('SearchBar', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
  });

  it('renders search input and button', () => {
    render(
      <BrowserRouter>
        <SearchBar />
      </BrowserRouter>
    );

    const input = screen.getByPlaceholderText('Search');
    const button = screen.getByRole('button', { name: '' });

    expect(input).toBeInTheDocument();
    expect(button).toBeInTheDocument();
  });

  it('updates input value when typing', () => {
    render(
      <BrowserRouter>
        <SearchBar />
      </BrowserRouter>
    );

    const input = screen.getByPlaceholderText('Search') as HTMLInputElement;
    fireEvent.change(input, { target: { value: 'test query' } });

    expect(input.value).toBe('test query');
  });

  it('navigates to search page with query on submit', () => {
    render(
      <BrowserRouter>
        <SearchBar />
      </BrowserRouter>
    );

    const input = screen.getByPlaceholderText('Search');
    const form = input.closest('form')!;

    fireEvent.change(input, { target: { value: 'react tutorial' } });
    fireEvent.submit(form);

    expect(mockNavigate).toHaveBeenCalledWith('/search?q=react%20tutorial');
  });

  it('does not navigate when query is empty', () => {
    render(
      <BrowserRouter>
        <SearchBar />
      </BrowserRouter>
    );

    const input = screen.getByPlaceholderText('Search');
    const form = input.closest('form')!;

    fireEvent.submit(form);

    expect(mockNavigate).not.toHaveBeenCalled();
  });

  it('trims whitespace from query before submitting', () => {
    render(
      <BrowserRouter>
        <SearchBar />
      </BrowserRouter>
    );

    const input = screen.getByPlaceholderText('Search');
    const form = input.closest('form')!;

    fireEvent.change(input, { target: { value: '  trimmed  ' } });
    fireEvent.submit(form);

    expect(mockNavigate).toHaveBeenCalledWith('/search?q=trimmed');
  });
});


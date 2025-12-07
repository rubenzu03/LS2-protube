import { renderHook } from '@testing-library/react';
import { useDocumentTitle } from '@/hooks/use-document-title';

describe('useDocumentTitle', () => {
  const originalTitle = document.title;

  beforeEach(() => {
    document.title = 'Original Title';
  });

  afterEach(() => {
    document.title = originalTitle;
  });

  it('sets document title when title is provided', () => {
    renderHook(() => useDocumentTitle('New Title'));
    expect(document.title).toBe('New Title');
  });

  it('restores previous title on unmount', () => {
    const { unmount } = renderHook(() => useDocumentTitle('New Title'));
    expect(document.title).toBe('New Title');

    unmount();
    expect(document.title).toBe('Original Title');
  });

  it('does not change title when undefined is passed', () => {
    const currentTitle = document.title;
    renderHook(() => useDocumentTitle(undefined));
    expect(document.title).toBe(currentTitle);
  });

  it('updates title when title prop changes', () => {
    const { rerender } = renderHook(({ title }) => useDocumentTitle(title), {
      initialProps: { title: 'First Title' }
    });

    expect(document.title).toBe('First Title');

    rerender({ title: 'Second Title' });
    expect(document.title).toBe('Second Title');
  });
});

import { useEffect, useRef } from 'react';

export function useDocumentTitle(title?: string) {
  const prevTitleRef = useRef<string>(typeof document !== 'undefined' ? document.title : '');

  useEffect(() => {
    if (title == null) return;
    const prev = prevTitleRef.current;
    document.title = title;

    return () => {
      document.title = prev;
    };
  }, [title]);
}

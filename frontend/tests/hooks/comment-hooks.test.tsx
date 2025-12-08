import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useVideoComments, useCreateComment } from '@/hooks/comment-hooks';
import { api } from '@/utils/api';
import type { Comment } from '@/types/comments';

jest.mock('@/utils/api');

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false
      }
    }
  });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

describe('comment-hooks', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('useVideoComments', () => {
    it('fetches comments successfully', async () => {
      const mockComments: Comment[] = [
        {
          id: 1,
          content: 'Great video!',
          username: 'user1',
          userId: 'user1',
          videoId: 1,
          createdAt: '2024-01-01'
        }
      ];

      (api.get as jest.Mock).mockResolvedValueOnce({ data: mockComments });

      const { result } = renderHook(() => useVideoComments('1'), {
        wrapper: createWrapper()
      });

      expect(result.current.loading).toBe('loading');

      await waitFor(() => {
        expect(result.current.loading).toBe('success');
      });

      expect(result.current.comments).toEqual(mockComments);
      expect(result.current.message).toBe('');
      expect(api.get).toHaveBeenCalledWith('/comments/video/1');
    });

    it('handles error state', async () => {
      (api.get as jest.Mock).mockRejectedValueOnce(new Error('Failed to fetch'));

      const { result } = renderHook(() => useVideoComments('1'), {
        wrapper: createWrapper()
      });

      await waitFor(() => {
        expect(result.current.loading).toBe('error');
      });

      expect(result.current.message).toContain('Error fetching comments');
      expect(result.current.comments).toEqual([]);
    });

    it('returns idle state when videoId is undefined', () => {
      const { result } = renderHook(() => useVideoComments(undefined), {
        wrapper: createWrapper()
      });

      expect(result.current.loading).toBe('idle');
      expect(api.get).not.toHaveBeenCalled();
    });

    it('returns idle state when videoId is empty string', () => {
      const { result } = renderHook(() => useVideoComments(''), {
        wrapper: createWrapper()
      });

      expect(result.current.loading).toBe('idle');
      expect(api.get).not.toHaveBeenCalled();
    });
  });

  describe('useCreateComment', () => {
    it('creates a comment successfully', async () => {
      const mockComment: Comment = {
        id: 1,
        content: 'New comment',
        username: 'user1',
        userId: 'user1',
        videoId: 1,
        createdAt: '2024-01-01'
      };

      (api.post as jest.Mock).mockResolvedValueOnce({ data: mockComment });

      const { result } = renderHook(() => useCreateComment('1'), {
        wrapper: createWrapper()
      });

      await waitFor(() => {
        expect(result.current).toBeDefined();
      });

      result.current.mutate({ content: 'New comment', videoId: 1 });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(api.post).toHaveBeenCalledWith('/comments', {
        content: 'New comment',
        userId: null,
        videoId: 1
      });
    });

    it('handles undefined videoId', () => {
      const { result } = renderHook(() => useCreateComment(undefined), {
        wrapper: createWrapper()
      });

      expect(result.current).toBeDefined();
    });

    it('handles error when creating comment', async () => {
      (api.post as jest.Mock).mockRejectedValueOnce(new Error('Failed to create'));

      const { result } = renderHook(() => useCreateComment('1'), {
        wrapper: createWrapper()
      });

      result.current.mutate({ content: 'New comment', videoId: 1 });

      await waitFor(() => {
        expect(result.current.isError).toBe(true);
      });
    });
  });
});

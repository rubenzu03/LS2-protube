import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useAllVideos, useChannelData, useSearchVideos } from '@/hooks/video-hooks';
import { api } from '@/utils/api';
import type { Video, User } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

jest.mock('@/utils/api');

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

describe('video-hooks', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('useAllVideos', () => {
    it('fetches all videos and thumbnails successfully', async () => {
      const mockVideos: Video[] = [
        {
          id: '1',
          title: 'Video 1',
          description: 'Description 1',
          uploader: 'user1',
          uploaderId: 'user1',
          uploadDate: '2024-01-01',
          views: 100,
          likes: 10,
          isLiked: false,
        },
      ];

      const mockThumbnails: Thumbnail[] = [
        { id: '1', filename: 'thumb1.jpg' },
      ];

      (api.get as jest.Mock)
        .mockResolvedValueOnce({ data: mockVideos })
        .mockResolvedValueOnce({ data: mockThumbnails });

      const { result } = renderHook(() => useAllVideos(), {
        wrapper: createWrapper(),
      });

      expect(result.current.loading).toBe('loading');

      await waitFor(() => {
        expect(result.current.loading).toBe('success');
      });

      expect(result.current.videos).toEqual(mockVideos);
      expect(result.current.thumbnails).toEqual(mockThumbnails);
      expect(result.current.message).toBe('');
    });

    it('handles error state', async () => {
      const errorMessage = 'Network error';
      (api.get as jest.Mock).mockRejectedValueOnce(new Error(errorMessage));

      const { result } = renderHook(() => useAllVideos(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.loading).toBe('error');
      });

      expect(result.current.message).toContain('Error fetching videos');
      expect(result.current.videos).toEqual([]);
      expect(result.current.thumbnails).toEqual([]);
    });
  });

  describe('useChannelData', () => {
    it('fetches channel data successfully', async () => {
      const mockUser: User = {
        id: 'user1',
        username: 'testuser',
        email: 'test@example.com',
      };

      const mockVideos: Video[] = [
        {
          id: '1',
          title: 'Video 1',
          description: 'Description 1',
          uploader: 'testuser',
          uploaderId: 'user1',
          userId: 'user1',
          uploadDate: '2024-01-01',
          views: 100,
          likes: 10,
          isLiked: false,
        },
        {
          id: '2',
          title: 'Video 2',
          description: 'Description 2',
          uploader: 'otheruser',
          uploaderId: 'user2',
          userId: 'user2',
          uploadDate: '2024-01-02',
          views: 200,
          likes: 20,
          isLiked: false,
        },
      ];

      const mockThumbnails: Thumbnail[] = [
        { id: '1', filename: 'thumb1.jpg' },
        { id: '2', filename: 'thumb2.jpg' },
      ];

      (api.get as jest.Mock)
        .mockResolvedValueOnce({ data: mockUser })
        .mockResolvedValueOnce({ data: mockVideos })
        .mockResolvedValueOnce({ data: mockThumbnails });

      const { result } = renderHook(() => useChannelData('user1'), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.loading).toBe('success');
      });

      expect(result.current.user).toEqual(mockUser);
      expect(result.current.videos).toHaveLength(1);
      expect(result.current.videos[0].id).toBe('1');
      expect(result.current.thumbnails).toHaveLength(1);
    });

    it('handles error state', async () => {
      (api.get as jest.Mock).mockRejectedValueOnce(new Error('Not found'));

      const { result } = renderHook(() => useChannelData('user1'), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.loading).toBe('error');
      });

      expect(result.current.message).toContain('Error fetching channel data');
      expect(result.current.user).toBeNull();
    });

    it('does not fetch when userId is empty', () => {
      const { result } = renderHook(() => useChannelData(''), {
        wrapper: createWrapper(),
      });

      expect(result.current.loading).toBe('idle');
      expect(api.get).not.toHaveBeenCalled();
    });
  });

  describe('useSearchVideos', () => {
    it('searches videos successfully', async () => {
      const mockVideos: Video[] = [
        {
          id: '1',
          title: 'Test Video',
          description: 'Test',
          uploader: 'user1',
          uploaderId: 'user1',
          uploadDate: '2024-01-01',
          views: 100,
          likes: 10,
          isLiked: false,
        },
      ];

      const mockThumbnails: Thumbnail[] = [
        { id: '1', filename: 'thumb1.jpg' },
      ];

      (api.get as jest.Mock)
        .mockResolvedValueOnce({ data: mockVideos })
        .mockResolvedValueOnce({ data: mockThumbnails });

      const { result } = renderHook(() => useSearchVideos('test'), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.loading).toBe('success');
      });

      expect(result.current.videos).toEqual(mockVideos);
      expect(result.current.thumbnails).toHaveLength(1);
      expect(api.get).toHaveBeenCalledWith('/videos/search?q=test');
    });

    it('returns idle state when query is empty', () => {
      const { result } = renderHook(() => useSearchVideos(''), {
        wrapper: createWrapper(),
      });

      expect(result.current.loading).toBe('idle');
      expect(result.current.message).toBe('Type something to search.');
      expect(api.get).not.toHaveBeenCalled();
    });

    it('handles whitespace-only query', () => {
      const { result } = renderHook(() => useSearchVideos('   '), {
        wrapper: createWrapper(),
      });

      expect(result.current.loading).toBe('idle');
      expect(api.get).not.toHaveBeenCalled();
    });

    it('handles error state', async () => {
      (api.get as jest.Mock).mockRejectedValueOnce(new Error('Search failed'));

      const { result } = renderHook(() => useSearchVideos('test'), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.loading).toBe('error');
      });

      expect(result.current.message).toContain('Error fetching videos');
    });
  });
});


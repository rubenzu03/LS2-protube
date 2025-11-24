import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';
import { useAllVideos } from '@/hooks/video-hooks';
import { api } from '@/utils/api';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

jest.mock('@/utils/api');

const mockVideos: Video[] = [
  {
    id: '1',
    title: 'Video 1',
    description: 'Description 1',
    duration: 120,
    width: 1920,
    height: 1080,
    filename: 'video1.mp4',
    userId: 'user1',
    categoryId: 'cat1',
    tagId: 'tag1',
    commentId: 'comment1'
  },
  {
    id: '2',
    title: 'Video 2',
    description: 'Description 2',
    duration: 180,
    width: 1920,
    height: 1080,
    filename: 'video2.mp4',
    userId: 'user2',
    categoryId: 'cat2',
    tagId: 'tag2',
    commentId: 'comment2'
  }
];

const mockThumbnails: Thumbnail[] = [
  { id: '1', filename: 'thumb1.jpg' },
  { id: '2', filename: 'thumb2.jpg' }
];

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false
      }
    }
  });

  return ({ children }: { children: React.ReactNode }) =>
    React.createElement(QueryClientProvider, { client: queryClient }, children);
};

describe('useAllVideos', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('returns loading state initially', () => {
    (api.get as jest.Mock).mockImplementation(() => new Promise(() => {}));

    const { result } = renderHook(() => useAllVideos(), {
      wrapper: createWrapper()
    });

    expect(result.current.loading).toBe('loading');
    expect(result.current.videos).toEqual([]);
    expect(result.current.thumbnails).toEqual([]);
  });

  it('returns videos and thumbnails on success', async () => {
    (api.get as jest.Mock)
      .mockResolvedValueOnce({ data: mockVideos })
      .mockResolvedValueOnce({ data: mockThumbnails });

    const { result } = renderHook(() => useAllVideos(), {
      wrapper: createWrapper()
    });

    await waitFor(() => {
      expect(result.current.loading).toBe('success');
    });

    expect(result.current.videos).toEqual(mockVideos);
    expect(result.current.thumbnails).toEqual(mockThumbnails);
  });

  it('returns error state on failure', async () => {
    (api.get as jest.Mock).mockRejectedValue(new Error('Network error'));

    const { result } = renderHook(() => useAllVideos(), {
      wrapper: createWrapper()
    });

    await waitFor(() => {
      expect(result.current.loading).toBe('error');
    });

    expect(result.current.message).toContain('Error fetching videos');
    expect(result.current.videos).toEqual([]);
    expect(result.current.thumbnails).toEqual([]);
  });

  it('calls correct API endpoints', async () => {
    (api.get as jest.Mock)
      .mockResolvedValueOnce({ data: mockVideos })
      .mockResolvedValueOnce({ data: mockThumbnails });

    renderHook(() => useAllVideos(), {
      wrapper: createWrapper()
    });

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith('/videos');
      expect(api.get).toHaveBeenCalledWith('/videos/thumbnails');
    });
  });
});

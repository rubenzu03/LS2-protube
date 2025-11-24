import { useQuery } from '@tanstack/react-query';
import { api } from '@/utils/api';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

type GetAllVideosResponse = {
  videos: Video[];
  thumbnails: Thumbnail[];
};

type SearchVideosResponse = {
  videos: Video[];
  thumbnails: Thumbnail[];
};

export function useAllVideos() {
  const { data, isLoading, isError, isSuccess, error } = useQuery({
    queryKey: ['videos&thumbnails'],
    queryFn: async (): Promise<GetAllVideosResponse> => {
      const [videosResponse, thumbnailsResponse] = await Promise.all([
        api.get<Video[]>('/videos'),
        api.get<Thumbnail[]>('/videos/thumbnails')
      ]);
      return { videos: videosResponse.data, thumbnails: thumbnailsResponse.data };
    }
  });

  const loading: LoadingState = isLoading
    ? 'loading'
    : isError
      ? 'error'
      : isSuccess
        ? 'success'
        : 'idle';

  const message = isError ? 'Error fetching videos: ' + error.message : isLoading ? 'Loading...' : '';

  return { videos: data?.videos ?? [], thumbnails: data?.thumbnails ?? [], message, loading };
}

export function useSearchVideos(query: string) {
  const trimmedQuery = query.trim();
  const { data, isLoading, isError, isSuccess, error } = useQuery({
    queryKey: ['searchVideos', trimmedQuery],
    queryFn: async (): Promise<SearchVideosResponse> => {
      const [videosResponse, thumbnailsResponse] = await Promise.all([
        api.get<Video[]>(`/videos/search?q=${encodeURIComponent(trimmedQuery)}`),
        api.get<Thumbnail[]>('/videos/thumbnails')
      ]);
      const videos = videosResponse.data;
      const thumbnailIds = new Set(videos.map((video) => String(video.id)));
      const thumbnails = thumbnailsResponse.data.filter((thumbnail) => thumbnailIds.has(String(thumbnail.id)));
      return { videos, thumbnails };
    },
    enabled: trimmedQuery.length > 0
  });

  const loading: LoadingState = trimmedQuery.length === 0
    ? 'idle'
    : isLoading
      ? 'loading'
      : isError
        ? 'error'
        : isSuccess
          ? 'success'
          : 'idle';

  const message = trimmedQuery.length === 0
    ? 'Type something to search.'
    : isError
      ? 'Error fetching videos: ' + error.message
      : isLoading
        ? 'Loading...'
        : '';

  return { videos: data?.videos ?? [], thumbnails: data?.thumbnails ?? [], message, loading };
}

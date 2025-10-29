import axios from 'axios';
import { useQuery } from '@tanstack/react-query';
import { getEnv } from '../utils/env';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

type GetAllVideosResponse = {
  videos: Video[];
  thumbnails: Thumbnail[];
};

export function useAllVideos() {
  const { data, isLoading, isError, isSuccess, error } = useQuery({
    queryKey: ['videos&thumbnails'],
    queryFn: async (): Promise<GetAllVideosResponse> => {
      const [videosResponse, thumbnailsResponse] = await Promise.all([
        axios.get<Video[]>(`${getEnv().API_BASE_URL}/videos`),
        axios.get<Thumbnail[]>(`${getEnv().API_BASE_URL}/videos/thumbnails`)
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

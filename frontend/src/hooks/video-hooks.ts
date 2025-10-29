import axios from 'axios';
import { useQuery } from '@tanstack/react-query';
import { getEnv } from '../utils/env';
import type { Video } from '@/types/videos';

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

export function useAllVideos() {
  const { data, isLoading, isError, isSuccess, error } = useQuery({
    queryKey: ['videos'],
    queryFn: async (): Promise<Video[]> => {
      const response = await axios.get<Video[]>(`${getEnv().API_BASE_URL}/videos`);
      return response.data;
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
  const value = (data as Video[]) ?? [];

  return { value, message, loading };
}

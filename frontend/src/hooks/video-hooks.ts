import axios, { AxiosError } from 'axios';
import { useQuery } from '@tanstack/react-query';
import { getEnv } from '../utils/Env';

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

export function useAllVideos() {
  const { data, isLoading, isError, isSuccess, error } = useQuery<string[], AxiosError>({
    queryKey: ['videos'],
    queryFn: async () => {
      const response = await axios.get<string[]>(`${getEnv().API_BASE_URL}/videos`);
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
  const value = data ?? [];

  return { value, message, loading };
}

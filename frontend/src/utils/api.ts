import axios from 'axios';
import { getEnv } from './env';
import type { Video } from '@/types/videos';

export const api = axios.create({
  baseURL: getEnv().API_BASE_URL
});

export const getVideo = async (id: string) => {
  const response = await api.get<Video>(`/videos/${id}`);
  return response.data;
};

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

export type Thumbnail = {
  id: string | number;
  filename: string;
};

export const getThumbnails = async () => {
  const response = await api.get<Thumbnail[]>('/videos/thumbnails');
  return response.data;
};

export const getThumbnail = (id: string | number) => {
  return `${getEnv().API_BASE_URL}/videos/thumbnail/${id}`;
};

export const getVideoStreamUrl = (id: string | number) => {
  return `${getEnv().API_BASE_URL}/videos/stream/${id}`;
};

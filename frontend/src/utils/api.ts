import axios from 'axios';
import { getEnv } from './Env';
import type { Video } from '@/types/videos';

const { API_BASE_URL } = getEnv();

export const api = axios.create({
  baseURL: API_BASE_URL
});

const TOKEN_KEY = 'protube_token';

api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = window.localStorage.getItem(TOKEN_KEY);
    if (token) {
      if (!config.headers) {
        config.headers = {} as any;
      }
      if (!('Authorization' in config.headers)) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
  }
  return config;
});

export const getVideo = async (id: string) => {
  const response = await api.get<Video>(`/videos/${id}`);
  return response.data;
};

export const getVideoPageData = async (id: string) => {
  const response = await api.get<Video>(`/videos/${id}`);
  const thumbnailResponse = getThumbnail(id);
  return { video: response.data, thumbnail: thumbnailResponse };
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

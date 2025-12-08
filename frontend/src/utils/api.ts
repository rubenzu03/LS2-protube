import axios, { type InternalAxiosRequestConfig } from 'axios';
import { getEnv } from './Env';
import type { Video } from '@/types/videos';

const TOKEN_KEY = 'protube_token';
const { API_BASE_URL } = getEnv();

export const api = axios.create({
  baseURL: API_BASE_URL
});

export const clearAuthToken = () => {
  if (typeof window !== 'undefined') {
    window.localStorage.removeItem(TOKEN_KEY);
  }
  delete api.defaults.headers.common.Authorization;
};

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  if (typeof window !== 'undefined') {
    const token = window.localStorage.getItem(TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error)) {
      const status = error.response?.status;
      if (status === 401 || status === 403) {
        clearAuthToken();
      }
    }
    return Promise.reject(error);
  }
);

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

export type UploadVideoPayload = {
  file: File;
  title: string;
  description: string;
};

export const uploadVideo = async ({ file, title, description }: UploadVideoPayload) => {
  const formData = new FormData();
  formData.append('file', file, file.name);
  formData.append('title', title || '');
  formData.append('description', description || '');
  const response = await api.post<Video>('/videos/upload', formData);
  return response.data;
};

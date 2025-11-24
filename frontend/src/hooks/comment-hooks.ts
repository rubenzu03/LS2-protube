import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { api } from '@/utils/api';
import type { Comment } from '@/types/comments';

type LoadingState = 'loading' | 'success' | 'error' | 'idle';

type CreateCommentPayload = {
  content: string;
  videoId: number;
};

export function useVideoComments(videoId?: string) {
  const enabled = !!videoId;
  const { data, isLoading, isError, isSuccess, error } = useQuery({
    queryKey: ['comments', videoId],
    enabled,
    queryFn: async (): Promise<Comment[]> => {
      const id = Number(videoId);
      const response = await api.get<Comment[]>(`/comments/video/${id}`);
      return response.data;
    }
  });

  const loading: LoadingState = !enabled
    ? 'idle'
    : isLoading
      ? 'loading'
      : isError
        ? 'error'
        : isSuccess
          ? 'success'
          : 'idle';

  const message = isError ? 'Error fetching comments: ' + error.message : '';

  return { comments: data ?? [], loading, message };
}

export function useCreateComment(videoId?: string) {
  const queryClient = useQueryClient();
  const numericId = videoId ? Number(videoId) : undefined;

  return useMutation({
    mutationFn: async ({ content, videoId }: CreateCommentPayload) => {
      const payload = {
        content,
        userId: null,
        videoId
      };
      const response = await api.post<Comment>('/comments', payload);
      return response.data;
    },
    onSuccess: () => {
      if (numericId != null && !Number.isNaN(numericId)) {
        queryClient.invalidateQueries({ queryKey: ['comments', String(numericId)] });
      }
    }
  });
}

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';

import { useVideoComments, useCreateComment } from '@/hooks/comment-hooks';
import { Button } from '@/components/ui/button';
import type { Comment } from '@/types/comments';

type Props = {
  videoId?: string;
};

const TOKEN_KEY = 'protube_token';

function getUsernameFromToken(token: string | null): string | null {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload));
    return decoded.sub || null;
  } catch {
    return null;
  }
}

export function CommentsSection({ videoId }: Props) {
  const { comments, loading } = useVideoComments(videoId);
  const createComment = useCreateComment(videoId);

  const [content, setContent] = useState('');
  const [hasToken, setHasToken] = useState<boolean>(() => {
    return typeof window !== 'undefined' && !!window.localStorage.getItem(TOKEN_KEY);
  });
  const [username, setUsername] = useState<string | null>(() => {
    if (typeof window === 'undefined') return null;
    return getUsernameFromToken(window.localStorage.getItem(TOKEN_KEY));
  });

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY);
    setHasToken(!!token);
    setUsername(getUsernameFromToken(token));

    const handleStorage = (event: StorageEvent) => {
      if (event.key === TOKEN_KEY) {
        setHasToken(!!event.newValue);
        setUsername(getUsernameFromToken(event.newValue));
      }
    };

    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  const handleSubmit = async () => {
    if (!videoId || !content.trim() || createComment.isPending) return;
    await createComment.mutateAsync({
      content: content.trim(),
      videoId: Number(videoId)
    });
    setContent('');
  };

  return (
    <section className="mt-6 flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Comments</h2>
        <span className="text-xs text-muted-foreground">{comments.length} comments</span>
      </div>

      {hasToken ? (
        <div className="flex gap-3">
          <div className="h-9 w-9 rounded-full bg-gradient-to-br from-indigo-600 to-indigo-800 flex items-center justify-center text-white text-sm font-bold">
            {username?.[0]?.toUpperCase() || '?'}
          </div>
          <div className="flex-1 space-y-2">
            <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              className="min-h-[72px] w-full resize-none rounded-xl border bg-background px-3 py-2 text-sm outline-none focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring"
              placeholder="Add a public comment..."
            />
            <div className="flex justify-end gap-2">
              <Button
                size="sm"
                variant="outline"
                onClick={() => setContent('')}
                disabled={createComment.isPending || !content.trim()}
              >
                Cancel
              </Button>
              <Button
                size="sm"
                onClick={handleSubmit}
                disabled={createComment.isPending || !content.trim()}
              >
                {createComment.isPending ? 'Commenting...' : 'Comment'}
              </Button>
            </div>
          </div>
        </div>
      ) : (
        <div className="flex items-center justify-between rounded-xl border border-dashed px-4 py-3 text-xs text-muted-foreground">
          <span>Sign in to join the conversation.</span>
          <Button
            size="sm"
            variant="outline"
            onClick={() => navigate('/auth', { state: { from: `/video/${videoId ?? ''}` } })}
          >
            Sign in
          </Button>
        </div>
      )}

      <div className="mt-2 flex flex-col gap-4">
        {loading === 'loading' && (
          <p className="text-xs text-muted-foreground">Loading comments...</p>
        )}
        {loading === 'error' && (
          <p className="text-xs text-destructive">Failed to load comments.</p>
        )}
        {loading === 'success' && comments.length === 0 && (
          <p className="text-xs text-muted-foreground">No comments yet. Be the first to comment!</p>
        )}
        {comments.map((comment: Comment) => {
          const commentUsername = comment.username && comment.username.trim().length > 0
            ? comment.username
            : comment.userId != null
              ? `User #${comment.userId}`
              : 'Anonymous';
          return (
            <div key={comment.id} className="flex gap-3">
              <div className="h-9 w-9 rounded-full bg-gradient-to-br from-indigo-600 to-indigo-800 flex items-center justify-center text-white text-sm font-bold">
                {commentUsername[0]?.toUpperCase() || '?'}
              </div>
              <div className="flex-1 space-y-1">
                <p className="text-xs font-semibold">{commentUsername}</p>
                <p className="text-sm text-foreground">{comment.content}</p>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

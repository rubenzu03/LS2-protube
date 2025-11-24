import { useParams } from 'react-router';
import { Layout } from '@/components/layout/layout';
import { VideoCard } from '@/components/video-card';
import { useChannelData } from '@/hooks/video-hooks';
import { useDocumentTitle } from '@/hooks/use-document-title';
import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';
import { CheckBadgeIcon } from '@heroicons/react/24/solid';

export function ChannelPage() {
  const { id } = useParams<{ id: string }>();
  const { user, videos, thumbnails, loading, message } = useChannelData(id || '');

  useDocumentTitle(user ? `${user.username} - ProTube` : 'Channel - ProTube');

  if (loading === 'loading') {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <div className="flex flex-col items-center gap-3 text-center">
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-gray-300 border-t-indigo-600" />
          <p className="text-sm text-muted-foreground">Loading channel...</p>
        </div>
      </div>
    );
  }

  if (loading === 'error') {
    return (
      <div className="flex items-center justify-center h-screen w-full">
        <div className="flex flex-col items-center gap-2 p-3 rounded-lg border border-destructive">
          <h3 className="text-lg font-semibold text-destructive">Something went wrong</h3>
          <p className="text-sm text-muted-foreground">{message}</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center h-screen w-full">
        <div className="flex flex-col items-center gap-2 p-3 rounded-lg border">
          <h3 className="text-lg font-semibold">Channel not found</h3>
          <p className="text-sm text-muted-foreground">The channel you're looking for doesn't exist.</p>
        </div>
      </div>
    );
  }

  return (
    <Layout>
      <div className="space-y-8">
        {/* Channel Header */}
        <div className="flex items-center gap-6 rounded-xl bg-gradient-to-r from-primary/10 via-primary/5 to-background p-8 border border-border/50">
          <div className="h-32 w-32 overflow-hidden rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white text-5xl font-bold shadow-lg">
            {user.username ? user.username[0].toUpperCase() : '?'}
          </div>
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <h1 className="text-4xl font-bold">{user.username}</h1>
              <CheckBadgeIcon className="h-8 w-8 text-primary" />
            </div>
            <div className="flex gap-4 text-sm text-muted-foreground">
              <span>{videos.length} {videos.length === 1 ? 'video' : 'videos'}</span>
            </div>
          </div>
        </div>

        {/* Videos Section */}
        <div>
          <h2 className="text-2xl font-bold mb-6">Videos</h2>
          {videos.length === 0 ? (
            <div className="flex items-center justify-center py-16 text-center">
              <div className="flex flex-col items-center gap-2">
                <p className="text-lg text-muted-foreground">This channel hasn't uploaded any videos yet.</p>
              </div>
            </div>
          ) : (
            <div className="grid grid-cols-[repeat(auto-fill,minmax(380px,1fr))] gap-10">
              {videos.map((video: Video) => {
                const t = thumbnails.find((thumbnail: Thumbnail) => String(thumbnail.id) === String(video.id))
                  ?? ({ id: video.id, filename: '' } as Thumbnail);
                return <VideoCard key={video.id} video={video} thumbnail={t} />;
              })}
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}

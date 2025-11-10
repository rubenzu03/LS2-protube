import type { Video } from '@/types/videos';
import { VideoCard } from '@/components/video-card';
import { useAllVideos } from '@/hooks/video-hooks';
import { Layout } from '@/components/layout/layout';
import type { Thumbnail } from '@/utils/api';
import { useDocumentTitle } from '@/hooks/use-document-title';

export function Home() {
  const { loading, message, videos, thumbnails } = useAllVideos();

  useDocumentTitle('ProTube - Home');

  if (loading === 'loading') {
    return (
      <div className="flex h-screen w-full items-center justify-center">
        <div className="flex flex-col items-center gap-3 text-center">
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-gray-300 border-t-indigo-600" />
          <p className="text-sm text-muted-foreground">Loading videos...</p>
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

  return (
    <Layout>
      <div className="grid grid-cols-[repeat(auto-fill,minmax(380px,1fr))] gap-10">
        {videos.map((video: Video) => {
          const t = thumbnails.find((thumbnail: Thumbnail) => String(thumbnail.id) === String(video.id))
              ?? ({ id: video.id, filename: '' } as Thumbnail);
          return <VideoCard key={video.id} video={video} thumbnail={t} />;
        })}
      </div>
    </Layout>
  );
}

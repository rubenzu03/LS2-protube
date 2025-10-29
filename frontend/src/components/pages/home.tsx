import type { Video } from '@/types/videos';
import { VideoCameraIcon } from '@heroicons/react/24/solid';
import { VideoCard } from '@/components/video-card';
import { useAllVideos } from '@/hooks/video-hooks';
import { Layout } from '@/components/layout/layout';
import type { Thumbnail } from '@/utils/api';

export function Home() {
  const { loading, message, videos, thumbnails } = useAllVideos();

  if (loading === 'loading') {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <div className="flex flex-col items-center gap-3 text-center">
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-gray-300 border-t-indigo-600" />
          <p className="text-sm text-muted-foreground">Loading videos...</p>
        </div>
      </div>
    );
  }

  if (loading === 'error') {
    return (
      <div className="mx-auto max-w-6xl p-6">
        <div className="rounded-lg border border-red-300 bg-red-50 p-6">
          <h3 className="mb-2 text-lg font-semibold text-red-700">Something went wrong</h3>
          <p className="text-sm text-red-600">{message}</p>
        </div>
      </div>
    );
  }

  return (
    <Layout>
      <div className="mx-auto max-w-6xl p-6">
        <div className="mb-6 flex flex-col gap-2">
          <div className="flex items-center gap-2">
            <h2 className="text-2xl font-bold">Featured Videos</h2>
            <VideoCameraIcon className="h-6 w-6 text-indigo-600" />
          </div>
          <p className="text-sm text-muted-foreground">Explore our curated collection</p>
        </div>

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {videos.map((video: Video) => {
            const t = thumbnails.find((thumbnail: Thumbnail) => String(thumbnail.id) === String(video.id))
              ?? ({ id: video.id, filename: '' } as Thumbnail);
            return <VideoCard key={video.id} video={video} thumbnail={t} />;
          })}
        </div>
      </div>
    </Layout>
  );
}

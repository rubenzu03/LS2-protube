import type { Video } from '@/types/videos';
import { VideoCard } from '@/components/video-card';
import { useSearchVideos } from '@/hooks/video-hooks';
import { Layout } from '@/components/layout/layout';
import { type Thumbnail } from '@/utils/api';
import { useDocumentTitle } from '@/hooks/use-document-title';
import { useSearchParams } from 'react-router';

export function SearchPage() {
  const [searchParams] = useSearchParams();
  const query = (searchParams.get('q') ?? '').trim();

  const { videos, message, loading, thumbnails } = useSearchVideos(query);

  useDocumentTitle(query ? `ProTube - Search "${query}"` : 'ProTube - Search');

  if (!query) {
    return (
      <Layout>
        <div className="flex w-full justify-center py-24">
          <p className="text-muted-foreground">Type something in the search bar to find videos.</p>
        </div>
      </Layout>
    );
  }

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

  if (loading === 'success' && videos.length === 0) {
    return (
      <Layout>
        <div className="flex w-full justify-center py-24">
          <p className="text-muted-foreground">No results found for "{query}".</p>
        </div>
      </Layout>
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

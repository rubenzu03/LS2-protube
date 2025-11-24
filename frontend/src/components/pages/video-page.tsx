import { useParams } from 'react-router';
import { Layout } from '../layout/layout';
import { useQuery } from '@tanstack/react-query';
import { getVideoPageData, getVideoStreamUrl } from '@/utils/api';
import { ChevronRightIcon } from '@heroicons/react/24/solid';
import { useAllVideos } from '@/hooks/video-hooks';
import { VideoPlayer } from '@/components/video/video-player';
import { ChannelActions } from '@/components/video/channel-actions';
import { VideoDescription } from '@/components/video/video-description';
import { RecommendedList } from '@/components/video/recommended-list';
import { CommentsSection } from '@/components/video/comments-section';
import { useEffect } from 'react';
import { useDocumentTitle } from '@/hooks/use-document-title';

export function VideoPage() {
  const { id } = useParams();

  const { data, isLoading, error } = useQuery({
    queryKey: ['videoPage', id],
    queryFn: () => getVideoPageData(id!)
  });

  const { video, thumbnail } = data ?? { video: undefined, thumbnail: undefined };

  useDocumentTitle(`ProTube - ${video?.title}`);

  const { videos, thumbnails, loading: recLoading } = useAllVideos();

  const playerSrc = video ? getVideoStreamUrl(video.id) : undefined;

  useEffect(() => {
    window.scrollTo({ top: 0 });
  }, [id]);

  return (
    <Layout>
      <div className="mx-auto max-w-[1400px] px-6 py-6">
        <div className="flex gap-10">
          <div className="flex flex-1 flex-col gap-3 relative">
            {thumbnail && (
              <div className="absolute -top-9 -left-20 blur-[100px] opacity-15 aspect-video w-[1100px]">
                <img src={thumbnail} alt={video?.title} className="w-full h-full object-cover rounded-lg" />
              </div>
            )}
            <VideoPlayer key={id} src={playerSrc} isLoading={isLoading} error={(error as Error | undefined)?.message} />
            {video && (
              <h1 className="text-xl font-semibold leading-relaxed">{video.title}</h1>
            )}
            <ChannelActions uploaderId={video?.userId} />
            <VideoDescription description={video?.description} />
            <CommentsSection videoId={id} />
          </div>

          <div className="flex w-[450px] flex-col gap-4">
            {recLoading === 'loading' ? (
              <div className="flex items-center justify-center py-6">
                <div className="h-6 w-6 animate-spin rounded-full border-2 border-gray-300 border-t-indigo-600" />
              </div>
            ) : (
              <>
                <RecommendedList videos={videos} thumbnails={thumbnails} currentId={id} limit={8} />
                <button className="mt-1 flex items-center justify-center gap-1 text-sm text-muted-foreground hover:text-foreground">
                  Show more
                  <ChevronRightIcon className="h-4 w-4" />
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
}

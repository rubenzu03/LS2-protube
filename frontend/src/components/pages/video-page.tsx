import { useParams } from 'react-router';
import { Layout } from '../layout/layout';
import { useQuery } from '@tanstack/react-query';
import { getVideo, getVideoStreamUrl } from '@/utils/api';
import { ChevronRightIcon } from '@heroicons/react/24/solid';
import { useAllVideos } from '@/hooks/video-hooks';
import { VideoPlayer } from '@/components/video/video-player';
import { ChannelActions } from '@/components/video/channel-actions';
import { VideoDescription } from '@/components/video/video-description';
import { RecommendedList } from '@/components/video/recommended-list';

export function VideoPage() {
  const { id } = useParams();

  const { data, isLoading, error } = useQuery({
    queryKey: ['video', id],
    queryFn: () => getVideo(id!)
  });

  const { videos, thumbnails, loading: recLoading } = useAllVideos();

  const playerSrc = data ? getVideoStreamUrl(data.id) : undefined;

  return (
    <Layout>
      <div className="mx-auto max-w-[1400px] px-6 py-6">
        <div className="flex gap-6">
          <div className="flex flex-1 flex-col gap-3">
            <VideoPlayer key={id} src={playerSrc} isLoading={isLoading} error={(error as Error | undefined)?.message} />

            {data && (
              <h1 className="text-xl font-semibold leading-relaxed">{data.title}</h1>
            )}

            <ChannelActions uploaderId={data?.userId} />

            <VideoDescription description={data?.description} />
          </div>

          <div className="flex w-[380px] flex-col gap-4">
            <div className="flex items-center gap-3 border-b border-border">
              <button className="border-b-2 border-foreground px-3 pb-2 text-sm font-medium">All</button>
              <button className="px-3 pb-2 text-sm font-medium text-muted-foreground hover:text-foreground">Related</button>
              <button className="px-3 pb-2 text-sm font-medium text-muted-foreground hover:text-foreground">Channel</button>
            </div>

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

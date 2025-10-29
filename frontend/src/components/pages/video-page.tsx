import { useParams, Link } from 'react-router';
import { Layout } from '../layout/layout';
import { useQuery } from '@tanstack/react-query';
import { getVideo } from '@/utils/api';
import { getEnv } from '@/utils/env';
import { Button } from '@/components/ui/button';
import {
  HandThumbUpIcon,
  HandThumbDownIcon,
  ShareIcon,
  ArrowDownTrayIcon,
  PlusCircleIcon,
  EllipsisHorizontalIcon,
  ChevronRightIcon
} from '@heroicons/react/24/solid';
import { useAllVideos } from '@/hooks/video-hooks';

export function VideoPage() {
  const { id } = useParams();

  const { data, isLoading, error } = useQuery({
    queryKey: ['video', id],
    queryFn: () => getVideo(id!)
  });

  const { value: recommended, loading: recLoading } = useAllVideos();

  return (
    <Layout>
      <div className="mx-auto max-w-[1400px] px-6 py-6">
        <div className="flex gap-6">
          {/* Left Column - Player and Info */}
          <div className="flex flex-1 flex-col gap-3">
            {/* Player */}
            <div className="aspect-video w-full overflow-hidden rounded-xl bg-black">
              {isLoading ? (
                <div className="flex h-full w-full items-center justify-center">
                  <div className="h-10 w-10 animate-spin rounded-full border-4 border-gray-300 border-t-indigo-600" />
                </div>
              ) : error ? (
                <div className="flex h-full w-full items-center justify-center text-sm text-red-500">
                  {(error as Error).message}
                </div>
              ) : data ? (
                <video className="h-full w-full" controls preload="metadata">
                  <source src={`${getEnv().API_BASE_URL}/videos/stream/${data.id}`} type="video/mp4" />
                  Your browser does not support the video tag.
                </video>
              ) : null}
            </div>

            {/* Title */}
            {data && (
              <h1 className="text-xl font-semibold leading-relaxed">{data.title}</h1>
            )}

            {/* Channel Info and Actions */}
            <div className="flex items-center justify-between">
              {/* Simple Channel Section */}
              <div className="flex items-center gap-3">
                <div className="h-10 w-10 overflow-hidden rounded-full bg-muted">
                  <img
                    src="/abstract-channel-avatar.png"
                    alt="Channel avatar"
                    className="h-full w-full object-cover"
                  />
                </div>
                <div className="flex flex-col">
                  <span className="text-sm font-semibold">Uploader</span>
                  <span className="max-w-[200px] truncate text-xs text-muted-foreground">
                    {data?.userId ?? 'Unknown'}
                  </span>
                </div>
                <Button className="ml-4 font-medium rounded-full bg-white text-black hover:bg-white/90 dark:bg-white dark:text-black">
                  Subscribe
                </Button>
              </div>

              {/* Actions */}
              <div className="flex items-center gap-2">
                <div className="flex items-center overflow-hidden rounded-full bg-accent/50">
                  <Button variant="ghost" className="rounded-none px-4 hover:bg-accent">
                    <HandThumbUpIcon className="h-5 w-5" />
                    <span className="text-sm font-medium">Like</span>
                  </Button>
                  <div className="h-6 w-px bg-border" />
                  <Button variant="ghost" className="rounded-none px-4 hover:bg-accent">
                    <HandThumbDownIcon className="h-5 w-5" />
                  </Button>
                </div>

                <Button variant="ghost" className="rounded-full px-4 hover:bg-accent">
                  <ShareIcon className="h-5 w-5" />
                  <span className="text-sm font-medium">Share</span>
                </Button>

                <Button variant="ghost" className="rounded-full px-4 hover:bg-accent">
                  <ArrowDownTrayIcon className="h-5 w-5" />
                  <span className="text-sm font-medium">Download</span>
                </Button>

                <Button variant="ghost" className="rounded-full px-4 hover:bg-accent">
                  <PlusCircleIcon className="h-5 w-5" />
                  <span className="text-sm font-medium">Thanks</span>
                </Button>

                <Button variant="ghost" className="rounded-full px-3 hover:bg-accent">
                  <EllipsisHorizontalIcon className="h-5 w-5" />
                </Button>
              </div>
            </div>

            {/* Description */}
            {data && (
              <div className="rounded-xl bg-accent/40 p-3 text-sm text-muted-foreground">
                {data.description}
              </div>
            )}
          </div>

          {/* Right Sidebar */}
          <div className="flex w-[380px] flex-col gap-4">
            {/* Tabs */}
            <div className="flex items-center gap-3 border-b border-border">
              <button className="border-b-2 border-foreground px-3 pb-2 text-sm font-medium">All</button>
              <button className="px-3 pb-2 text-sm font-medium text-muted-foreground hover:text-foreground">Related</button>
              <button className="px-3 pb-2 text-sm font-medium text-muted-foreground hover:text-foreground">Channel</button>
            </div>

            {/* Recommended */}
            <div className="flex flex-col gap-3">
              {recLoading === 'loading' ? (
                <div className="flex items-center justify-center py-6">
                  <div className="h-6 w-6 animate-spin rounded-full border-2 border-gray-300 border-t-indigo-600" />
                </div>
              ) : (
                recommended
                  .filter((v) => v.id !== id)
                  .slice(0, 8)
                  .map((video) => (
                    <Link to={`/video/${video.id}`} key={video.id} className="group flex cursor-pointer gap-2">
                      <div className="relative aspect-video w-[168px] flex-shrink-0 overflow-hidden rounded-lg bg-muted">
                        {/* Placeholder thumb with duration */}
                        <div className="absolute bottom-1 right-1 rounded bg-black/80 px-1 text-xs font-semibold text-white">
                          {Math.floor(video.duration / 60)}:{Math.floor(video.duration % 60).toString().padStart(2, '0')}
                        </div>
                      </div>
                      <div className="flex flex-1 flex-col gap-1">
                        <h3 className="line-clamp-2 text-sm font-medium leading-tight group-hover:text-foreground/90">
                          {video.title}
                        </h3>
                        <p className="line-clamp-2 text-xs text-muted-foreground">{video.description}</p>
                      </div>
                      <Button variant="ghost" size="icon" className="h-8 w-8 opacity-0 group-hover:opacity-100">
                        <EllipsisHorizontalIcon className="h-4 w-4" />
                      </Button>
                    </Link>
                  ))
              )}
              <button className="flex items-center justify-center gap-1 text-sm text-muted-foreground hover:text-foreground">
                Show more
                <ChevronRightIcon className="h-4 w-4" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

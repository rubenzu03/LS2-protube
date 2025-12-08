import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';
import { getThumbnail, getVideoStreamUrl } from '@/utils/api';
import { Link } from 'react-router';
import { CheckBadgeIcon } from '@heroicons/react/24/solid';
import { useRef, useState } from 'react';
import { useUserInfo } from '@/hooks/video-hooks';

type Props = {
  video: Video;
  thumbnail: Thumbnail;
};
const PREVIEW_DELAY_MS = 1000;

export function VideoCard({ video, thumbnail }: Props) {
  const durationLabel = `${Math.floor(video.duration / 60)}:${Math.floor(video.duration % 60)
    .toString()
    .padStart(2, '0')}`;
  const { user } = useUserInfo(video.userId);
  const channelLabel = user?.username || 'Channel';
  const [showPreview, setShowPreview] = useState(false);
  const hoverTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const previewRef = useRef<HTMLVideoElement | null>(null);
  const previewSrc = getVideoStreamUrl(video.id);
  return (
    <Link
      to={`/video/${video.id}`}
      className="group block overflow-hidden rounded-xl bg-background transition-shadow duration-300 hover:shadow-[0_0_100px_rgba(0,0,0,10)] shadow-foreground/10 dark:shadow-foreground/5"
      onMouseEnter={() => {
        if (hoverTimerRef.current) clearTimeout(hoverTimerRef.current);
        hoverTimerRef.current = setTimeout(() => {
          setShowPreview(true);
          const el = previewRef.current;
          if (el) {
            el.currentTime = 0;
            el.play().catch(() => { });
          }
        }, PREVIEW_DELAY_MS);
      }}
      onMouseLeave={() => {
        if (hoverTimerRef.current) {
          clearTimeout(hoverTimerRef.current);
          hoverTimerRef.current = null;
        }
        setShowPreview(false);
        const el = previewRef.current;
        if (el) {
          el.pause();
          el.currentTime = 0;
        }
      }}
    >
      <div className="relative aspect-video w-full overflow-hidden rounded-xl">
        <img src={getThumbnail(thumbnail.id)} alt={video.title} className="h-full w-full object-cover" loading="lazy" />
        <video
          ref={previewRef}
          className={`pointer-events-none absolute inset-0 h-full w-full object-cover transition-opacity duration-200 ${showPreview ? 'opacity-100' : 'opacity-0'}`}
          muted
          playsInline
          preload="metadata"
          src={previewSrc}
        />
        <div className="absolute bottom-2 right-2 rounded bg-black/80 px-1 text-xs font-semibold text-white">
          {durationLabel}
        </div>
      </div>

      <div className="flex gap-3 p-3">
        <Link
          to={`/channel/${video.userId}`}
          className="mt-0.5 h-9 w-9 overflow-hidden rounded-full bg-muted hover:opacity-80 transition-opacity"
          onClick={(e) => e.stopPropagation()}
        >
          <img src="/abstract-channel-avatar.png" alt={channelLabel} className="h-full w-full object-cover" />
        </Link>
        <div className="min-w-0 flex-1">
          <h3 className="line-clamp-2 text-[15px] font-semibold leading-snug group-hover:text-foreground/90">
            {video.title}
          </h3>
          <div className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
            <Link
              to={`/channel/${video.userId}`}
              className="truncate hover:text-foreground/80 transition-colors"
              onClick={(e) => e.stopPropagation()}
            >
              {channelLabel}
            </Link>
            <CheckBadgeIcon className="h-3.5 w-3.5 text-muted-foreground/70" />
          </div>
        </div>
      </div>
    </Link>
  );
}

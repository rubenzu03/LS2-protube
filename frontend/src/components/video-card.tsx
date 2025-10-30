import type { Video } from '@/types/videos';
import type { Thumbnail } from '@/utils/api';
import { getThumbnail } from '@/utils/api';
import { Link } from 'react-router';
import { CheckBadgeIcon } from '@heroicons/react/24/solid';

type Props = {
  video: Video;
  thumbnail: Thumbnail;
};

export function VideoCard({ video, thumbnail }: Props) {
  const durationLabel = `${Math.floor(video.duration / 60)}:${Math.floor(video.duration % 60)
    .toString()
    .padStart(2, '0')}`;
  const channelLabel = video.userId || 'Channel';
  return (
    <Link to={`/video/${video.id}`} className="group block overflow-hidden rounded-xl bg-background transition-shadow duration-300 hover:shadow-[0_0_100px_rgba(0,0,0,10)] shadow-foreground/10 dark:shadow-foreground/5">
      <div className="relative aspect-video w-full overflow-hidden rounded-xl">
        <img src={getThumbnail(thumbnail.id)} alt={video.title} className="h-full w-full object-cover" loading="lazy" />
        <div className="absolute bottom-2 right-2 rounded bg-black/80 px-1 text-xs font-semibold text-white">
          {durationLabel}
        </div>
      </div>

      <div className="flex gap-3 p-3">
        <div className="mt-0.5 h-9 w-9 overflow-hidden rounded-full bg-muted">
          <img src="/abstract-channel-avatar.png" alt={channelLabel} className="h-full w-full object-cover" />
        </div>
        <div className="min-w-0 flex-1">
          <h3 className="line-clamp-2 text-[15px] font-semibold leading-snug group-hover:text-foreground/90">
            {video.title}
          </h3>
          <div className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
            <span className="truncate hover:text-foreground/80">{channelLabel}</span>
            <CheckBadgeIcon className="h-3.5 w-3.5 text-muted-foreground/70" />
          </div>
        </div>
      </div>
    </Link>
  );
}

import { Link } from 'react-router';
import { Button } from '@/components/ui/button';
import { EllipsisHorizontalIcon } from '@heroicons/react/24/solid';
import type { Video } from '@/types/videos';
import { getThumbnail, type Thumbnail } from '@/utils/api';

type Props = {
  videos: Video[];
  thumbnails: Thumbnail[];
  currentId?: string;
  limit?: number;
};

function formatDuration(totalSeconds: number) {
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = Math.floor(totalSeconds % 60).toString().padStart(2, '0');
  return `${minutes}:${seconds}`;
}

export function RecommendedList({ videos, thumbnails, currentId, limit = 8 }: Props) {
  const items = videos.filter((v) => v.id !== currentId).slice(0, limit);
  return (
    <div className="flex flex-col gap-3">
      {items.map((video) => (
        <Link to={`/video/${video.id}`} key={video.id} className="group flex cursor-pointer gap-2">
          <div className="relative aspect-video w-[168px] flex-shrink-0 overflow-hidden rounded-lg bg-muted">
            <img
              src={getThumbnail(thumbnails.find((thumbnail: Thumbnail) => String(thumbnail.id) === String(video.id))?.id ?? '')}
              alt={video.title}
              className="h-full w-full object-cover"
              loading="lazy"
            />
            <div className="absolute bottom-1 right-1 rounded bg-black/80 px-1 text-xs font-semibold text-white">
              {formatDuration(video.duration)}
            </div>
          </div>
          <div className="flex flex-1 flex-col gap-1">
            <h3 className="line-clamp-2 text-sm font-medium leading-tight group-hover:text-foreground/90">{video.title}</h3>
            <p className="line-clamp-2 text-xs text-muted-foreground">{video.description}</p>
          </div>
          <Button variant="ghost" size="icon" className="h-8 w-8 opacity-0 group-hover:opacity-100">
            <EllipsisHorizontalIcon className="h-4 w-4" />
          </Button>
        </Link>
      ))}
    </div>
  );
}

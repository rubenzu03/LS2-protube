import type { Video } from '@/types/videos';
import { Link } from 'react-router';

type Props = {
  video: Video;
  src: string;
};

export function VideoCard({ video, src }: Props) {
  return (
    <div className="overflow-hidden rounded-lg border bg-background shadow-sm hover:shadow-md transition-shadow duration-300">
      <div className="aspect-video w-full bg-black">
        <video className="h-full w-full" controls preload="metadata">
          <source src={src} type="video/mp4" />
          Your browser does not support the video tag.
        </video>
      </div>

      <div className="space-y-2 p-4">
        <Link to={`/video/${video.id}`}>
          <div className="flex items-start justify-between gap-3">
            <h3 className="line-clamp-2 text-base font-semibold leading-tight">{video.title}</h3>
            <span className="shrink-0 rounded bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
              {Math.floor(video.duration / 60)}:{Math.floor(video.duration % 60).toString().padStart(2, '0')}
            </span>
          </div>
          <p className="line-clamp-2 text-sm text-muted-foreground">{video.description}</p>
        </Link>
      </div>
    </div>
  );
}

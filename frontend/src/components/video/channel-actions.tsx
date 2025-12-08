import { Button } from '@/components/ui/button';
import { Link } from 'react-router';
import {
  HandThumbUpIcon,
  HandThumbDownIcon,
  ShareIcon,
  ArrowDownTrayIcon,
  PlusCircleIcon,
  EllipsisHorizontalIcon
} from '@heroicons/react/24/solid';
import { useUserInfo } from '@/hooks/video-hooks';

type Props = {
  uploaderId?: string;
};

export function ChannelActions({ uploaderId }: Props) {
  const { user } = useUserInfo(uploaderId);
  const uploaderName = user?.username || 'Unknown';

  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-3">
        <Link to={`/channel/${uploaderId}`}>
          <div className="h-10 w-10 overflow-hidden rounded-full bg-gradient-to-br from-indigo-600 to-indigo-800 flex items-center justify-center text-white text-sm font-bold hover:opacity-80 transition-opacity">
            {uploaderName[0]?.toUpperCase() || '?'}
          </div>
        </Link>
        <Link to={`/channel/${uploaderId}`}>
          <div className="flex flex-col">
            <span className="text-sm font-semibold">{uploaderName}</span>
            <span className="max-w-[200px] truncate text-xs text-muted-foreground">@{uploaderName.toLowerCase().replace(/\s+/g, '')}</span>
          </div>
        </Link>
        <Button className="ml-4 font-medium rounded-full bg-primary text-primary-foreground hover:bg-primary/90">
          Subscribe
        </Button>
      </div>

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
          <span className="text-sm font-medium">Add to playlist</span>
        </Button>

        <Button variant="ghost" className="rounded-full px-3 hover:bg-accent">
          <EllipsisHorizontalIcon className="h-5 w-5" />
        </Button>
      </div>
    </div>
  );
}

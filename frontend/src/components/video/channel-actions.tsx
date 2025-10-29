import { Button } from '@/components/ui/button';
import {
  HandThumbUpIcon,
  HandThumbDownIcon,
  ShareIcon,
  ArrowDownTrayIcon,
  PlusCircleIcon,
  EllipsisHorizontalIcon
} from '@heroicons/react/24/solid';

type Props = {
  uploaderId?: string;
};

export function ChannelActions({ uploaderId }: Props) {
  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-3">
        <div className="h-10 w-10 overflow-hidden rounded-full bg-muted">
          <img src="/abstract-channel-avatar.png" alt="Channel avatar" className="h-full w-full object-cover" />
        </div>
        <div className="flex flex-col">
          <span className="text-sm font-semibold">Uploader</span>
          <span className="max-w-[200px] truncate text-xs text-muted-foreground">{uploaderId ?? 'Unknown'}</span>
        </div>
        <Button className="ml-4 font-medium rounded-full bg-white text-black hover:bg-white/90 dark:bg-white dark:text-black">
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
          <span className="text-sm font-medium">Thanks</span>
        </Button>

        <Button variant="ghost" className="rounded-full px-3 hover:bg-accent">
          <EllipsisHorizontalIcon className="h-5 w-5" />
        </Button>
      </div>
    </div>
  );
}

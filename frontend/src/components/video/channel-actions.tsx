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
      </div>


    </div>
  );
}

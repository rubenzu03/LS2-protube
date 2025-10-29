import { cn } from '@/lib/utils';
import { useEffect, useState } from 'react';
type Props = {
  src?: string;
  isLoading?: boolean;
  error?: string;
  poster?: string;
};

export function VideoPlayer({ src, isLoading, error, poster }: Props) {
  const [isReady, setIsReady] = useState(false);
  useEffect(() => {
    setIsReady(false);
  }, [src]);

  return (
    <div className="relative aspect-video w-full overflow-hidden rounded-xl bg-black duration-500 transition-all">
      {src && !error ? (
        <video
          className={
            cn('absolute inset-0 h-full w-full transition-opacity duration-500',
              isReady && !isLoading ? 'opacity-100' : 'opacity-0')
          }
          controls={isReady}
          preload="auto"
          poster={isReady ? poster : undefined}
          onCanPlay={() => setIsReady(true)}
          onCanPlayThrough={() => setIsReady(true)}
          onLoadedData={() => setIsReady(true)}
        >
          <source src={src} type="video/mp4" />
        </video>
      ) : null}
    </div>
  );
}

import { cn } from '@/lib/utils';
import { useEffect, useRef, useState } from 'react';
type Props = {
  src?: string;
  isLoading?: boolean;
  error?: string;
  poster?: string;
};

export function VideoPlayer({ src, isLoading, error, poster }: Props) {
  const [isReady, setIsReady] = useState(false);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const VOLUME_KEY = 'protube_volume';
  const [volume, setVolume] = useState(0.5);
  useEffect(() => {
    setIsReady(false);
  }, [src]);

  useEffect(() => {
    const stored = localStorage.getItem(VOLUME_KEY);
    if (stored != null) {
      const parsed = Math.max(0, Math.min(1, parseFloat(stored)));
      if (!Number.isNaN(parsed)) setVolume(parsed);
    }
  }, []);

  useEffect(() => {
    if (isReady && videoRef.current) {
      videoRef.current.play().catch(() => {});
      videoRef.current.volume = volume;
    }
  }, [isReady, volume]);

  return (
    <div className="group relative aspect-video w-[960px] max-w-full shrink-0 overflow-hidden rounded-xl bg-black duration-500 transition-all">
      {src && !error ? (
        <video
          ref={videoRef}
          className={
            cn('absolute inset-0 h-full w-full object-contain transition-opacity duration-500',
              '[&::-webkit-media-controls]:opacity-0 group-hover:[&::-webkit-media-controls]:opacity-100',
              '[&::-webkit-media-controls]:transition-opacity [&::-webkit-media-controls]:duration-300',
              isReady && !isLoading ? 'opacity-100' : 'opacity-0')
          }
          controls={isReady}
          autoPlay
          playsInline
          preload="auto"
          poster={isReady ? poster : undefined}
          onCanPlay={() => setIsReady(true)}
          onCanPlayThrough={() => setIsReady(true)}
          onLoadedData={() => setIsReady(true)}
          onVolumeChange={() => {
            if (!videoRef.current) return;
            const v = Math.max(0, Math.min(1, videoRef.current.volume));
            setVolume(v);
            localStorage.setItem(VOLUME_KEY, String(v));
          }}
        >
          <source src={src} type="video/mp4" />
        </video>
      ) : null}
    </div>
  );
}

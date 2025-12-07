import { cn } from '@/lib/utils';
import { useEffect, useRef, useState } from 'react';
type Props = {
  src?: string;
  isLoading?: boolean;
  error?: string;
  poster?: string;
  children?: React.ReactNode;
};

const MUTED_KEY = 'protube_muted';
const VOLUME_KEY = 'protube_volume';

export function VideoPlayer({ src, isLoading, error, poster }: Props) {
  const [isReady, setIsReady] = useState(false);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const getStoredVolume = () => {
    const stored = localStorage.getItem(VOLUME_KEY);
    const parsed = stored != null ? parseFloat(stored) : NaN;
    return Number.isFinite(parsed) ? Math.max(0, Math.min(1, parsed)) : 0.5;
  };
  const getStoredMuted = () => localStorage.getItem(MUTED_KEY) === 'true';
  const initialVolume = getStoredVolume();
  const initialMuted = getStoredMuted();
  const [volume, setVolume] = useState(initialVolume);
  const [muted, setMuted] = useState(initialMuted);
  const lastVolumeRef = useRef(initialVolume);
  const prevMutedRef = useRef(initialMuted);
  useEffect(() => {
    setIsReady(false);
  }, [src]);

  useEffect(() => {
    if (!isReady || !videoRef.current) return;
    const el = videoRef.current;
    el.muted = muted;
    el.volume = volume;
    if (el.paused) {
      el.play().catch(() => {});
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isReady]);

  useEffect(() => {
    if (!videoRef.current) return;
    videoRef.current.muted = muted;
    videoRef.current.volume = volume;
  }, [muted, volume]);

  return (
    <div className="group relative aspect-video w-[960px] max-w-full shrink-0 overflow-hidden rounded-xl bg-black duration-500 transition-all">
      {src && !error ? (
        <video
          ref={videoRef}
          data-testid="video-player"
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
          muted={muted}
          onCanPlay={() => setIsReady(true)}
          onCanPlayThrough={() => setIsReady(true)}
          onLoadedData={() => setIsReady(true)}
          onVolumeChange={() => {
            if (!videoRef.current) return;
            const el = videoRef.current;
            const newMuted = !!el.muted;
            const newVolume = Math.max(0, Math.min(1, el.volume));

            // If just unmuted and volume jumped to 1, restore last saved volume
            if (!newMuted && prevMutedRef.current) {
              const target = lastVolumeRef.current;
              if (Math.abs(newVolume - target) > 0.001) {
                el.volume = target;
              }
            }

            if (!newMuted) {
              setVolume(el.volume);
              localStorage.setItem(VOLUME_KEY, String(el.volume));
              lastVolumeRef.current = el.volume;
            }
            setMuted(newMuted);
            localStorage.setItem(MUTED_KEY, String(newMuted));
            prevMutedRef.current = newMuted;
          }}
        >
          <source src={src} type="video/mp4" />
        </video>
      ) : null}
    </div>
  );
}

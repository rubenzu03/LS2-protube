import { useAllVideos } from '@/hooks/video-hooks';
import { ModeToggle } from '../mode-toggle';

export function Home() {
  const { loading, message, value } = useAllVideos();
  return (
    <div>
      <ModeToggle />
      <div>
        {loading && <div>Loading...</div>}
        {message && <div>{message}</div>}
        {value && <div>{value.map((item) => (
          <div>{item}</div>
        ))}</div>}
      </div>
    </div>
  );
}

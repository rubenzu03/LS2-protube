import { useState, useEffect } from 'react';
import { getEnv } from '../utils/Env';
import { Video } from '../types/Video';

const VideoGrid = () => {
  const [videos, setVideos] = useState<Video[]>([]);

  useEffect(() => {
    fetch(getEnv().API_BASE_URL + '/videos')
      .then((res) => {
        return res.json();
      })
      .then((data) => {
        setVideos(data as Video[]);
      });
  }, []);

  return (
    <ul className="row g-4" style={{ listStyle: 'none', padding: 0 }}>
      {videos?.map((video) => (
        <li key={video.id} style={{ marginBottom: '20px' }}>
          <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px' }}>
            <h3>{video.title || `Video ${video.id}`}</h3>
            <video
              controls
              width="100%"
              style={{ maxWidth: '600px', borderRadius: '4px' }}
            >
              <source src={`${getEnv().API_BASE_URL}/videos/stream/${video.id}`} type="video/mp4" />
              Your browser does not support the video tag.
            </video>
            {video.description && <p>{video.description}</p>}
            {video.duration && <p>Duration: {Math.floor(video.duration)}s</p>}
          </div>
        </li>
      ))}
    </ul>
  );
};

export default VideoGrid;

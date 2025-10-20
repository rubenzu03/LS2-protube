import './App.css';
import { useAllVideos } from './useAllVideos';
import { getEnv } from './utils/Env';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="logo" />
        <ContentApp />
      </header>
    </div>
  );
}

function ContentApp() {
  const { loading, message, value } = useAllVideos();
  switch (loading) {
    case 'loading':
      return <div>Loading...</div>;
    case 'error':
      return (
        <div>
          <h3>Error</h3> <p>{message}</p>
        </div>
      );
    case 'success':
      return (
        <>
          <strong>Videos available:</strong>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(400px, 1fr))', gap: '20px', marginTop: '20px' }}>
            {value.map((video) => (
              <div key={video.id} style={{ border: '1px solid #ccc', borderRadius: '8px', padding: '10px' }}>
                <h3>{video.title || `Video ${video.id}`}</h3>
                <video
                  controls
                  width="100%"
                  style={{ maxWidth: '400px', borderRadius: '4px' }}
                >
                  <source src={`${getEnv().API_BASE_URL}/videos/stream/${video.id}`} type="video/mp4" />
                  Your browser does not support the video tag.
                </video>
                {video.description && <p>{video.description}</p>}
                {video.duration && <p>Duration: {Math.floor(video.duration)}s</p>}
              </div>
            ))}
          </div>
        </>
      );
  }
  return <div>Idle...</div>;
}

export default App;

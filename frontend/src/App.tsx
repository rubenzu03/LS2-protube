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
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', marginTop: '20px' }}>
            {value.map((video) => (
              <div key={video.id} style={{ border: '1px solid #ccc', padding: '10px', borderRadius: '8px' }}>
                <h3>{video.title}</h3>
                <p>{video.description}</p>
                <video
                  controls
                  width="640"
                  height="360"
                  style={{ maxWidth: '100%' }}
                >
                  <source src={`${getEnv().API_BASE_URL}/videos/stream/${video.id}`} type="video/mp4" />
                  Your browser does not support the video tag.
                </video>
                <p>Duration: {Math.floor(video.duration / 60)}:{Math.floor(video.duration % 60).toString().padStart(2, '0')}</p>
              </div>
            ))}
          </div>
        </>
      );
  }
  return <div>Idle...</div>;
}

export default App;

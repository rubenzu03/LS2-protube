import './App.css';
import { useAllVideos } from './useAllVideos';
import { getEnv } from './utils/Env';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="ProTube Logo" />
        <h1 className="header-title">ProTube</h1>
      </header>
      <ContentApp />
    </div>
  );
}

function ContentApp() {
  const { loading, message, value } = useAllVideos();
  
  switch (loading) {
    case 'loading':
      return (
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p className="loading-text">Loading amazing videos...</p>
        </div>
      );
      
    case 'error':
      return (
        <div className="error-container">
          <h3 className="error-title">⚠️ Oops! Something went wrong</h3>
          <p className="error-message">{message}</p>
        </div>
      );
      
    case 'success':
      return (
        <div className="videos-container">
          <h2 className="section-title">🎬 Featured Videos</h2>
          <div className="videos-grid">
            {value.map((video, index) => (
              <div key={video.id} className="video-card" style={{ animationDelay: `${index * 0.1}s` }}>
                <div className="video-content">
                  <h3 className="video-title">{video.title}</h3>
                  <p className="video-description">{video.description}</p>
                  
                  <div className="video-player-wrapper">
                    <video
                      className="video-player"
                      controls
                      preload="metadata"
                    >
                      <source 
                        src={`${getEnv().API_BASE_URL}/videos/stream/${video.id}`} 
                        type="video/mp4" 
                      />
                      Your browser does not support the video tag.
                    </video>
                  </div>
                  
                  <div className="video-meta">
                    <span className="duration-badge">
                      ⏱️ {Math.floor(video.duration / 60)}:{Math.floor(video.duration % 60).toString().padStart(2, '0')}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      );
  }
  
  return (
    <div className="loading-container">
      <p className="loading-text">Initializing...</p>
    </div>
  );
}

export default App;

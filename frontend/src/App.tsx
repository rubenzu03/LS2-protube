import './App.css';
import { useAllVideos } from './useAllVideos';
import { getEnv } from './utils/Env';
import { MoonIcon, SunIcon } from '@heroicons/react/24/outline';
import { VideoCameraIcon } from '@heroicons/react/24/solid';
import { useState, useEffect } from 'react';
import { Video } from './types/Video';

function App() {
  const [theme, setTheme] = useState<'light' | 'dark'>(() => {
    const saved = localStorage.getItem('theme');
    return (saved as 'light' | 'dark') || 'dark';
  });

  useEffect(() => {
    localStorage.setItem('theme', theme);
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  return (
    <div className="App">
      <header className="App-header">
        <div className="header-content">
          <img src="/protube-logo-removebg-preview.png" className="App-logo" alt="ProTube Logo" />
          <h1 className="header-title">ProTube</h1>
          <button onClick={toggleTheme} className="theme-toggle" aria-label="Toggle theme">
            {theme === 'light' ? <MoonIcon className="togle-theme-icon" /> : <SunIcon className="togle-theme-icon" />}
          </button>
        </div>
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
          <p className="loading-text">Loading videos...</p>
        </div>
      );

    case 'error':
      return (
        <div className="error-container">
          <h3 className="error-title">Something went wrong</h3>
          <p className="error-message">{message}</p>
        </div>
      );

    case 'success':
      return (
        <div className="videos-container">
          <div className="section-header">
            <div className="featured-videos">
              <h2 className="section-title">Featured Videos</h2>
              <VideoCameraIcon className="section-title-icon" />
            </div>
            <p className="section-description">Explore our curated collection</p>
          </div>
          <div className="videos-grid">
            {value.map((video: Video) => (
              <div key={video.id} className="video-card">
                <div className="video-player-wrapper">
                  <video className="video-player" controls preload="metadata">
                    <source src={`${getEnv().API_BASE_URL}/videos/stream/${video.id}`} type="video/mp4" />
                    Your browser does not support the video tag.
                  </video>
                </div>

                <div className="video-content">
                  <div className="video-header">
                    <h3 className="video-title">{video.title}</h3>
                    <span className="duration-badge">
                      {Math.floor(video.duration / 60)}:
                      {Math.floor(video.duration % 60)
                        .toString()
                        .padStart(2, '0')}
                    </span>
                  </div>
                  <p className="video-description">{video.description}</p>
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

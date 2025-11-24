import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Route, Routes } from 'react-router';
import { Home } from '@/components/pages/home';
import { ThemeProvider } from './components/theme-provider';
import { VideoPage } from './components/pages/video-page';
import { SearchPage } from './components/pages/search-page';
import { AuthPage } from '@/components/pages/auth';
import { ChannelPage } from './components/pages/channel-page';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/search" element={<SearchPage />} />
            <Route path="/video/:id" element={<VideoPage />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/channel/:id" element={<ChannelPage />} />
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}
export default App;

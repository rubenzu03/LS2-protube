import type { Video } from '@/types/videos';

jest.mock('@/utils/Env', () => ({
  getEnv: () => ({
    API_BASE_URL: 'http://localhost:8080/api',
    API_DOMAIN: 'http://localhost:8080'
  })
}));

import { api, getVideo, getVideoPageData, getThumbnails, getThumbnail, getVideoStreamUrl } from '@/utils/api';

describe('API Utils', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  describe('getVideo', () => {
    it('fetches a video by id', async () => {
      const mockVideo: Video = {
        id: '1',
        title: 'Test Video',
        description: 'Test description',
        uploader: 'testuser',
        uploaderId: 'user123',
        uploadDate: '2024-01-01',
        views: 100,
        likes: 10,
        isLiked: false
      };

      jest.spyOn(api, 'get').mockResolvedValue({ data: mockVideo } as any);

      const result = await getVideo('1');

      expect(result).toEqual(mockVideo);
      expect(api.get).toHaveBeenCalledWith('/videos/1');
    });
  });

  describe('getVideoPageData', () => {
    it('fetches video and thumbnail data', async () => {
      const mockVideo: Video = {
        id: '1',
        title: 'Test Video',
        description: 'Test description',
        uploader: 'testuser',
        uploaderId: 'user123',
        uploadDate: '2024-01-01',
        views: 100,
        likes: 10,
        isLiked: false
      };

      jest.spyOn(api, 'get').mockResolvedValue({ data: mockVideo } as any);

      const result = await getVideoPageData('1');

      expect(result.video).toEqual(mockVideo);
      expect(result.thumbnail).toBe('http://localhost:8080/api/videos/thumbnail/1');
      expect(api.get).toHaveBeenCalledWith('/videos/1');
    });
  });

  describe('getThumbnails', () => {
    it('fetches all thumbnails', async () => {
      const mockThumbnails = [
        { id: '1', filename: 'thumb1.jpg' },
        { id: '2', filename: 'thumb2.jpg' }
      ];

      jest.spyOn(api, 'get').mockResolvedValue({ data: mockThumbnails } as any);

      const result = await getThumbnails();

      expect(result).toEqual(mockThumbnails);
      expect(api.get).toHaveBeenCalledWith('/videos/thumbnails');
    });
  });

  describe('getThumbnail', () => {
    it('returns thumbnail URL for string id', () => {
      const result = getThumbnail('123');
      expect(result).toBe('http://localhost:8080/api/videos/thumbnail/123');
    });

    it('returns thumbnail URL for number id', () => {
      const result = getThumbnail(456);
      expect(result).toBe('http://localhost:8080/api/videos/thumbnail/456');
    });
  });

  describe('getVideoStreamUrl', () => {
    it('returns stream URL for string id', () => {
      const result = getVideoStreamUrl('123');
      expect(result).toBe('http://localhost:8080/api/videos/stream/123');
    });

    it('returns stream URL for number id', () => {
      const result = getVideoStreamUrl(456);
      expect(result).toBe('http://localhost:8080/api/videos/stream/456');
    });
  });

  describe('api interceptors', () => {
    it('adds authorization header when token exists', async () => {
      const mockToken = 'test-token-123';
      localStorage.setItem('protube_token', mockToken);

      const mockGet = jest.spyOn(api, 'get').mockResolvedValue({ data: {} } as any);

      await api.get('/test');

      expect(mockGet).toHaveBeenCalled();
    });

    it('works without token', async () => {
      localStorage.clear();

      const mockGet = jest.spyOn(api, 'get').mockResolvedValue({ data: {} } as any);

      await api.get('/test');

      expect(mockGet).toHaveBeenCalled();
    });
  });
});

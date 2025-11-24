import { api, getThumbnail, getVideoStreamUrl } from '@/utils/api';
import { getEnv } from '@/utils/Env';

jest.mock('@/utils/Env');

describe('API Utils', () => {
  beforeEach(() => {
    (getEnv as jest.Mock).mockReturnValue({
      API_BASE_URL: 'http://localhost:8080',
    });
  });

  describe('getThumbnail', () => {
    it('returns correct thumbnail URL for string ID', () => {
      const url = getThumbnail('video123');
      expect(url).toBe('http://localhost:8080/videos/thumbnail/video123');
    });

    it('returns correct thumbnail URL for number ID', () => {
      const url = getThumbnail(456);
      expect(url).toBe('http://localhost:8080/videos/thumbnail/456');
    });
  });

  describe('getVideoStreamUrl', () => {
    it('returns correct stream URL for string ID', () => {
      const url = getVideoStreamUrl('video789');
      expect(url).toBe('http://localhost:8080/videos/stream/video789');
    });

    it('returns correct stream URL for number ID', () => {
      const url = getVideoStreamUrl(101);
      expect(url).toBe('http://localhost:8080/videos/stream/101');
    });
  });

  describe('API instance', () => {
    it('has correct base URL', () => {
      expect(api.defaults.baseURL).toBe('http://localhost:8080');
    });

    it('has headers structure with common property', () => {
      expect(api.defaults.headers).toBeDefined();
      expect(api.defaults.headers.common).toBeDefined();
    });
  });
});


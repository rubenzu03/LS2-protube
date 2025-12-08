export const api = {
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
  defaults: {
    baseURL: 'http://localhost:8080',
    headers: {
      common: {
        Authorization: undefined
      }
    }
  },
  interceptors: {
    request: {
      use: jest.fn(),
      handlers: []
    },
    response: {
      use: jest.fn()
    }
  }
};

export const getThumbnail = jest.fn((id: string | number) => `http://localhost:8080/api/videos/thumbnail/${id}`);
export const getVideoStreamUrl = jest.fn((id: string | number) => `http://localhost:8080/api/videos/stream/${id}`);

// These functions need to call through to api.get, so we'll implement them properly
export const getVideo = jest.fn(async (id: string) => {
  const response = await api.get(`/videos/${id}`);
  return response.data;
});

export const getVideoPageData = jest.fn(async (id: string) => {
  const response = await api.get(`/videos/${id}`);
  const thumbnailResponse = getThumbnail(id);
  return { video: response.data, thumbnail: thumbnailResponse };
});

export const getThumbnails = jest.fn(async () => {
  const response = await api.get('/videos/thumbnails');
  return response.data;
});

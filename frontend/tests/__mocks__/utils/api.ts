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

export const getThumbnail = jest.fn((id: string | number) => `http://localhost:8080/videos/thumbnail/${id}`);
export const getVideoStreamUrl = jest.fn((id: string | number) => `http://localhost:8080/videos/stream/${id}`);
export const getVideo = jest.fn();
export const getVideoPageData = jest.fn();
export const getThumbnails = jest.fn();

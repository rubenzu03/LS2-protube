import '@testing-library/jest-dom';
import { TextEncoder, TextDecoder } from 'util';

// Polyfill for TextEncoder/TextDecoder in jsdom
global.TextEncoder = TextEncoder as typeof global.TextEncoder;
global.TextDecoder = TextDecoder as typeof global.TextDecoder;

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn()
  }))
});

// Mock localStorage with actual storage implementation
const storage: Record<string, string> = {};
const localStorageMock: Storage = {
  getItem: jest.fn((key: string) => storage[key] || null),
  setItem: jest.fn((key: string, value: string) => {
    storage[key] = value;
  }),
  removeItem: jest.fn((key: string) => {
    delete storage[key];
  }),
  clear: jest.fn(() => {
    Object.keys(storage).forEach(key => delete storage[key]);
  }),
  get length() {
    return Object.keys(storage).length;
  },
  key: jest.fn((index: number) => {
    const keys = Object.keys(storage);
    return keys[index] || null;
  })
};
Object.defineProperty(global, 'localStorage', {
  value: localStorageMock,
  writable: true
});
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
  writable: true
});

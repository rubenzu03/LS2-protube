export default {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  roots: ['<rootDir>/tests', '<rootDir>/src'],
  testMatch: ['**/__tests__/**/*.ts?(x)', '**/?(*.)+(spec|test).ts?(x)'],
  moduleNameMapper: {
    '^@/utils/api$': '<rootDir>/tests/__mocks__/utils/api.ts',
    '^@/utils/Env$': '<rootDir>/tests/__mocks__/utils/Env.ts',
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
    '\\.(jpg|jpeg|png|gif|svg)$': '<rootDir>/tests/__mocks__/fileMock.js'
  },
  transform: {
    '^.+\\.tsx?$': ['ts-jest', {
      tsconfig: 'tsconfig.test.json'
    }]
  },
  setupFilesAfterEnv: ['<rootDir>/tests/setupTests.ts'],
  collectCoverageFrom: [
    'src/**/*.{ts,tsx}',
    '!src/**/*.d.ts',
    '!src/main.tsx',
    '!src/vite-env.d.ts',
    '!src/**/*.test.{ts,tsx}',
    '!src/setupTests.ts'
  ],
  coveragePathIgnorePatterns: [
    '/node_modules/',
    '\\.test\\.(ts|tsx)$',
    'src/utils/Env.ts'
  ],
  transformIgnorePatterns: [
    'node_modules/(?!(react-router)/)'
  ],
  modulePathIgnorePatterns: [],
  coverageThreshold: {
    global: {
      branches: 10,
      functions: 15,
      lines: 25,
      statements: 25
    }
  }
};


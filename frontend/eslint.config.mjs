import js from '@eslint/js';
import tseslint from 'typescript-eslint';

export default tseslint.config(
  js.configs.recommended,
  ...tseslint.configs.recommended,
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      parser: tseslint.parser,
      parserOptions: {
        project: ['./tsconfig.app.json', './tsconfig.node.json', './tsconfig.configs.json'],
      },
    },
    plugins: {
      '@typescript-eslint': tseslint.plugin,
    },
    rules: {
      '@typescript-eslint/no-unused-vars': 'warn',
      // 'no-console': 'warn',
      semi: ['error', 'always'],
      'no-extra-semi': 'error',
      quotes: ['error', 'single', { avoidEscape: true }],
      indent: ['error', 2],
      'comma-dangle': ['error', 'never'],
      'space-before-function-paren': ['error', { anonymous: 'always', named: 'never', asyncArrow: 'always' }],
      'space-before-blocks': ['error', 'always'],
      'keyword-spacing': ['error', { before: true, after: true }],
      'object-curly-spacing': ['error', 'always'],
      'array-bracket-spacing': ['error', 'never'],
      'comma-spacing': ['error', { before: false, after: true }],
      'no-trailing-spaces': 'error',
      'eol-last': ['error', 'always'],
      'no-multiple-empty-lines': ['error', { max: 1, maxEOF: 0 }],
      'padded-blocks': ['error', 'never'],
      'space-infix-ops': 'error',
      camelcase: ['error', { properties: 'never' }],
      'no-var': 'error',
      'prefer-const': 'error',
    },
  },
  {
    files: ['**/*.{tsx,jsx}'],
    rules: {
      indent: 'off',
    },
  }
);

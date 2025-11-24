export const getEnv = () => {
  const { VITE_API_DOMAIN, VITE_MEDIA_DOMAIN, ...otherViteConfig } = import.meta.env;

  return {
    // full API & media base URLs used by axios and media helpers
    API_BASE_URL: `${VITE_API_DOMAIN}/api`,
    MEDIA_BASE_URL: `${VITE_MEDIA_DOMAIN}/media`,
    // raw domains for endpoints that live outside /api (e.g. /auth)
    API_DOMAIN: VITE_API_DOMAIN,
    MEDIA_DOMAIN: VITE_MEDIA_DOMAIN,
    __vite__: otherViteConfig
  };
};

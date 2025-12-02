import { setDocumentTitle, setMeta, setMetaProperty } from '@/utils/meta';

describe('meta utils', () => {
  beforeEach(() => {
    document.head.innerHTML = '';
    document.title = '';
  });

  describe('setDocumentTitle', () => {
    it('sets document title', () => {
      setDocumentTitle('Test Title');
      expect(document.title).toBe('Test Title');
    });
  });

  describe('setMeta', () => {
    it('creates meta tag if it does not exist', () => {
      setMeta('description', 'Test description');
      const meta = document.querySelector('meta[name="description"]');
      expect(meta).toBeInTheDocument();
      expect(meta?.getAttribute('content')).toBe('Test description');
    });

    it('updates existing meta tag', () => {
      setMeta('description', 'First description');
      setMeta('description', 'Updated description');

      const metas = document.querySelectorAll('meta[name="description"]');
      expect(metas.length).toBe(1);
      expect(metas[0].getAttribute('content')).toBe('Updated description');
    });
  });

  describe('setMetaProperty', () => {
    it('creates meta property tag if it does not exist', () => {
      setMetaProperty('og:title', 'Test OG Title');
      const meta = document.querySelector('meta[property="og:title"]');
      expect(meta).toBeInTheDocument();
      expect(meta?.getAttribute('content')).toBe('Test OG Title');
    });

    it('updates existing meta property tag', () => {
      setMetaProperty('og:title', 'First OG Title');
      setMetaProperty('og:title', 'Updated OG Title');

      const metas = document.querySelectorAll('meta[property="og:title"]');
      expect(metas.length).toBe(1);
      expect(metas[0].getAttribute('content')).toBe('Updated OG Title');
    });
  });
});

import { useState, type ChangeEvent, type FormEvent } from 'react';
import { useNavigate } from 'react-router';
import { Layout } from '@/components/layout/layout';
import { Button } from '@/components/ui/button';
import { useUploadVideo } from '@/hooks/video-hooks';
import { useDocumentTitle } from '@/hooks/use-document-title';

export function UploadPage() {
  const [file, setFile] = useState<File | null>(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const uploadMutation = useUploadVideo();

  useDocumentTitle('ProTube - Upload video');

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const f = event.target.files?.[0] ?? null;
    setFile(f);
    if (f && !title) {
      const base = f.name.replace(/\.[^/.]+$/, '');
      setTitle(base);
    }
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!file) {
      setError('Please choose a video file to upload.');
      return;
    }

    if (!title.trim()) {
      setError('Please add a title for your video.');
      return;
    }

    try {
      const result = await uploadMutation.mutateAsync({
        file,
        title: title.trim(),
        description: description.trim()
      });

      if (result?.id != null) {
        navigate(`/video/${result.id}`);
      } else {
        navigate('/');
      }
    } catch (err) {
      setError('Could not upload your video. Please try again.');
    }
  };

  const isUploading = uploadMutation.isPending;

  return (
    <Layout>
      <div className="flex w-full justify-center py-8">
        <div className="flex w-full max-w-3xl flex-col gap-6 rounded-2xl border bg-card px-10 py-8 shadow-xl">
          <div className="flex flex-col gap-1">
            <h2 className="text-2xl font-semibold tracking-tight">Upload video</h2>
            <p className="text-sm text-muted-foreground">
              Share your content with the world. Choose a video file, add a title and an optional description,
              then publish it.
            </p>
          </div>

          <form className="flex flex-col gap-6" onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 gap-6 md:grid-cols-[2fr,3fr]">
              <label className="flex h-40 cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border-2 border-dashed border-muted-foreground/40 bg-muted/40 text-center text-sm text-muted-foreground hover:border-indigo-500 hover:bg-muted/70">
                <span className="text-xs font-medium uppercase tracking-wide text-indigo-400">Video file</span>
                <span className="text-sm font-medium">
                  {file ? file.name : 'Click to select a video'}
                </span>
                <span className="text-[11px] text-muted-foreground/80">MP4 up to 100MB</span>
                <input
                  type="file"
                  accept="video/mp4,video/*"
                  className="hidden"
                  onChange={handleFileChange}
                />
              </label>

              <div className="flex flex-col gap-4">
                <div className="flex flex-col gap-1.5">
                  <label className="text-xs font-medium text-muted-foreground">Title</label>
                  <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="w-full rounded-md border bg-background px-3 py-2 text-sm outline-none focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring"
                    placeholder="Add a title that describes your video"
                  />
                </div>

                <div className="flex flex-col gap-1.5">
                  <label className="text-xs font-medium text-muted-foreground">Description</label>
                  <textarea
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    className="min-h-[96px] w-full resize-none rounded-md border bg-background px-3 py-2 text-sm outline-none focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring"
                    placeholder="Tell viewers about your video"
                  />
                </div>
              </div>
            </div>

            {error && (
              <p className="text-xs text-destructive bg-destructive/10 border border-destructive/40 rounded-md px-3 py-2">
                {error}
              </p>
            )}

            <div className="mt-2 flex items-center justify-end gap-3">
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate(-1)}
                disabled={isUploading}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={isUploading || !file}>
                {isUploading ? 'Uploading...' : 'Upload'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
}

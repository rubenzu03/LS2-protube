export type Comment = {
  id: number;
  content: string;
  userId: number | null;
  videoId: number | null;
  username?: string | null;
};

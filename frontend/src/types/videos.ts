export type Video = {
  id: string;
  title: string;
  description: string;
  duration: number;
  width: number;
  height: number;
  filename: string;
  userId: string;
  categoryId: string;
  tagId: string;
  commentId: string;
};

export type User = {
  id: string;
  username: string;
  videoId?: string;
  commentId?: string;
};

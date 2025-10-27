export interface Video {
  id: number;
  title: string;
  width: number;
  height: number;
  duration: number;
  description: string;
  filename: string;
  userId: number | null;
  categoryId: number | null;
  tagId: number | null;
  commentId: number | null;
}

export interface Video {
  id: number;
  title: string;
  width: number;
  height: number;
  duration: number;
  description: string;
  videoUrl: string;
  userId?: number;
  categoryId?: number;
  tags?: string[];
  commentId?: number;
}


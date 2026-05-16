export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  records: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CurrentUser {
  id: number;
  username: string;
  displayName: string;
  role: 'USER' | 'ADMIN';
}

export interface AuthPayload {
  accessToken: string;
  refreshToken: string;
  user: CurrentUser;
}

export interface Category {
  id: number;
  name: string;
  slug: string;
}

export interface Tag {
  id: number;
  name: string;
  slug: string;
}

export interface PostSummary {
  id: number;
  title: string;
  slug: string;
  summary: string;
  status: 'DRAFT' | 'PUBLISHED';
  pinned: boolean;
  viewCount: number;
  authorName: string;
  category: Category | null;
  tags: Tag[];
  publishedAt: string | null;
  createdAt: string;
}

export interface PostDetail extends PostSummary {
  contentMarkdown: string;
  contentHtml: string;
  authorId: number;
  updatedAt: string;
}

export interface CommentNode {
  id: number;
  authorName: string;
  content: string;
  status: 'PENDING' | 'APPROVED';
  guest: boolean;
  canDelete: boolean;
  createdAt: string;
  replies: CommentNode[];
}

export interface DashboardStats {
  users: number;
  posts: number;
  publishedPosts: number;
  pendingComments: number;
}

export interface AdminUser {
  id: number;
  username: string;
  displayName: string;
  role: 'USER' | 'ADMIN';
  enabled: boolean;
}

export interface AdminComment {
  id: number;
  postTitle: string;
  authorName: string;
  content: string;
  status: 'PENDING' | 'APPROVED';
  guest: boolean;
  createdAt: string;
}

import dayjs from 'dayjs/esm';
import { CommentType } from 'app/entities/enumerations/comment-type.model';

export interface IPostComment {
  id?: string;
  commentedBy?: string | null;
  comment?: string | null;
  commentType?: CommentType | null;
  commentedOn?: dayjs.Dayjs | null;
}

export class PostComment implements IPostComment {
  constructor(
    public id?: string,
    public commentedBy?: string | null,
    public comment?: string | null,
    public commentType?: CommentType | null,
    public commentedOn?: dayjs.Dayjs | null
  ) {}
}

export function getPostCommentIdentifier(postComment: IPostComment): string | undefined {
  return postComment.id;
}

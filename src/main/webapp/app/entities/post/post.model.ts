import dayjs from 'dayjs/esm';
import { ICenter } from 'app/entities/center/center.model';
import { IEmployee } from 'app/entities/employee/employee.model';

export interface IPost {
  id?: string;
  postDate?: dayjs.Dayjs | null;
  title?: string | null;
  body?: string | null;
  publish?: boolean | null;
  publishedOn?: dayjs.Dayjs | null;
  center?: ICenter | null;
  employee?: IEmployee | null;
}

export class Post implements IPost {
  constructor(
    public id?: string,
    public postDate?: dayjs.Dayjs | null,
    public title?: string | null,
    public body?: string | null,
    public publish?: boolean | null,
    public publishedOn?: dayjs.Dayjs | null,
    public center?: ICenter | null,
    public employee?: IEmployee | null
  ) {
    this.publish = this.publish ?? false;
  }
}

export function getPostIdentifier(post: IPost): string | undefined {
  return post.id;
}

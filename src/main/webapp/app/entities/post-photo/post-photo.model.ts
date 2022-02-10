import dayjs from 'dayjs/esm';
import { IPost } from 'app/entities/post/post.model';
import { IEmployee } from 'app/entities/employee/employee.model';

export interface IPostPhoto {
  id?: string;
  sequence?: number | null;
  title?: string;
  description?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  imageUrl?: string;
  uploadedOn?: dayjs.Dayjs | null;
  post?: IPost | null;
  uploadedBy?: IEmployee | null;
}

export class PostPhoto implements IPostPhoto {
  constructor(
    public id?: string,
    public sequence?: number | null,
    public title?: string,
    public description?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public imageUrl?: string,
    public uploadedOn?: dayjs.Dayjs | null,
    public post?: IPost | null,
    public uploadedBy?: IEmployee | null
  ) {}
}

export function getPostPhotoIdentifier(postPhoto: IPostPhoto): string | undefined {
  return postPhoto.id;
}

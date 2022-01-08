import { ICenter } from 'app/entities/center/center.model';

export interface ICenterImages {
  id?: string;
  imageContentType?: string | null;
  image?: string | null;
  title?: string | null;
  description?: string | null;
  show?: boolean | null;
  center?: ICenter | null;
}

export class CenterImages implements ICenterImages {
  constructor(
    public id?: string,
    public imageContentType?: string | null,
    public image?: string | null,
    public title?: string | null,
    public description?: string | null,
    public show?: boolean | null,
    public center?: ICenter | null
  ) {
    this.show = this.show ?? false;
  }
}

export function getCenterImagesIdentifier(centerImages: ICenterImages): string | undefined {
  return centerImages.id;
}

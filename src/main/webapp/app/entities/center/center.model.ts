import { IDivision } from 'app/entities/division/division.model';
import { IDistrict } from 'app/entities/district/district.model';
import { IUpazila } from 'app/entities/upazila/upazila.model';

export interface ICenter {
  id?: string;
  name?: string | null;
  addressLine?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  division?: IDivision | null;
  district?: IDistrict | null;
  upazila?: IUpazila | null;
}

export class Center implements ICenter {
  constructor(
    public id?: string,
    public name?: string | null,
    public addressLine?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public division?: IDivision | null,
    public district?: IDistrict | null,
    public upazila?: IUpazila | null
  ) {}
}

export function getCenterIdentifier(center: ICenter): string | undefined {
  return center.id;
}

import { IDistrict } from 'app/entities/district/district.model';

export interface IUpazila {
  id?: string;
  name?: string | null;
  bnName?: string | null;
  url?: string | null;
  district?: IDistrict | null;
}

export class Upazila implements IUpazila {
  constructor(
    public id?: string,
    public name?: string | null,
    public bnName?: string | null,
    public url?: string | null,
    public district?: IDistrict | null
  ) {}
}

export function getUpazilaIdentifier(upazila: IUpazila): string | undefined {
  return upazila.id;
}

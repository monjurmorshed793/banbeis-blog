import { IDivision } from 'app/entities/division/division.model';

export interface IDistrict {
  id?: string;
  name?: string | null;
  bnName?: string | null;
  lat?: string | null;
  lon?: string | null;
  url?: string | null;
  division?: IDivision | null;
}

export class District implements IDistrict {
  constructor(
    public id?: string,
    public name?: string | null,
    public bnName?: string | null,
    public lat?: string | null,
    public lon?: string | null,
    public url?: string | null,
    public division?: IDivision | null
  ) {}
}

export function getDistrictIdentifier(district: IDistrict): string | undefined {
  return district.id;
}

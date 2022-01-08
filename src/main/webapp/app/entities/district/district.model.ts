export interface IDistrict {
  id?: string;
  divisionId?: string | null;
  name?: string | null;
  bnName?: string | null;
  lat?: string | null;
  lon?: string | null;
  url?: string | null;
}

export class District implements IDistrict {
  constructor(
    public id?: string,
    public divisionId?: string | null,
    public name?: string | null,
    public bnName?: string | null,
    public lat?: string | null,
    public lon?: string | null,
    public url?: string | null
  ) {}
}

export function getDistrictIdentifier(district: IDistrict): string | undefined {
  return district.id;
}

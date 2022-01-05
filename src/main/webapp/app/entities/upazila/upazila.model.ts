export interface IUpazila {
  id?: string;
  districtId?: string | null;
  name?: string | null;
  bnName?: string | null;
  url?: string | null;
}

export class Upazila implements IUpazila {
  constructor(
    public id?: string,
    public districtId?: string | null,
    public name?: string | null,
    public bnName?: string | null,
    public url?: string | null
  ) {}
}

export function getUpazilaIdentifier(upazila: IUpazila): string | undefined {
  return upazila.id;
}

export interface IDivision {
  id?: string;
  name?: string | null;
  bnName?: string | null;
  url?: string | null;
}

export class Division implements IDivision {
  constructor(public id?: string, public name?: string | null, public bnName?: string | null, public url?: string | null) {}
}

export function getDivisionIdentifier(division: IDivision): string | undefined {
  return division.id;
}

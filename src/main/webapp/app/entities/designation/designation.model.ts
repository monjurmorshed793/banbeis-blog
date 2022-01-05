export interface IDesignation {
  id?: string;
  name?: string;
  sortName?: string;
  grade?: number | null;
}

export class Designation implements IDesignation {
  constructor(public id?: string, public name?: string, public sortName?: string, public grade?: number | null) {}
}

export function getDesignationIdentifier(designation: IDesignation): string | undefined {
  return designation.id;
}

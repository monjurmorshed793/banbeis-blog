import { IDesignation } from 'app/entities/designation/designation.model';

export interface IEmployee {
  id?: string;
  fullName?: string;
  bnFullName?: string;
  mobile?: string;
  email?: string;
  photoUrl?: string | null;
  photoContentType?: string | null;
  photo?: string | null;
  designation?: IDesignation | null;
}

export class Employee implements IEmployee {
  constructor(
    public id?: string,
    public fullName?: string,
    public bnFullName?: string,
    public mobile?: string,
    public email?: string,
    public photoUrl?: string | null,
    public photoContentType?: string | null,
    public photo?: string | null,
    public designation?: IDesignation | null
  ) {}
}

export function getEmployeeIdentifier(employee: IEmployee): string | undefined {
  return employee.id;
}

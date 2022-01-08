import dayjs from 'dayjs/esm';
import { IDesignation } from 'app/entities/designation/designation.model';
import { DutyType } from 'app/entities/enumerations/duty-type.model';

export interface ICenterEmployee {
  id?: string;
  dutyType?: DutyType | null;
  joiningDate?: dayjs.Dayjs | null;
  releaseDate?: dayjs.Dayjs | null;
  message?: string | null;
  designation?: IDesignation | null;
}

export class CenterEmployee implements ICenterEmployee {
  constructor(
    public id?: string,
    public dutyType?: DutyType | null,
    public joiningDate?: dayjs.Dayjs | null,
    public releaseDate?: dayjs.Dayjs | null,
    public message?: string | null,
    public designation?: IDesignation | null
  ) {}
}

export function getCenterEmployeeIdentifier(centerEmployee: ICenterEmployee): string | undefined {
  return centerEmployee.id;
}

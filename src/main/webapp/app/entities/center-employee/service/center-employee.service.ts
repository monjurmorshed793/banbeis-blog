import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICenterEmployee, getCenterEmployeeIdentifier } from '../center-employee.model';

export type EntityResponseType = HttpResponse<ICenterEmployee>;
export type EntityArrayResponseType = HttpResponse<ICenterEmployee[]>;

@Injectable({ providedIn: 'root' })
export class CenterEmployeeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/center-employees');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(centerEmployee: ICenterEmployee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(centerEmployee);
    return this.http
      .post<ICenterEmployee>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(centerEmployee: ICenterEmployee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(centerEmployee);
    return this.http
      .put<ICenterEmployee>(`${this.resourceUrl}/${getCenterEmployeeIdentifier(centerEmployee) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(centerEmployee: ICenterEmployee): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(centerEmployee);
    return this.http
      .patch<ICenterEmployee>(`${this.resourceUrl}/${getCenterEmployeeIdentifier(centerEmployee) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<ICenterEmployee>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICenterEmployee[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCenterEmployeeToCollectionIfMissing(
    centerEmployeeCollection: ICenterEmployee[],
    ...centerEmployeesToCheck: (ICenterEmployee | null | undefined)[]
  ): ICenterEmployee[] {
    const centerEmployees: ICenterEmployee[] = centerEmployeesToCheck.filter(isPresent);
    if (centerEmployees.length > 0) {
      const centerEmployeeCollectionIdentifiers = centerEmployeeCollection.map(
        centerEmployeeItem => getCenterEmployeeIdentifier(centerEmployeeItem)!
      );
      const centerEmployeesToAdd = centerEmployees.filter(centerEmployeeItem => {
        const centerEmployeeIdentifier = getCenterEmployeeIdentifier(centerEmployeeItem);
        if (centerEmployeeIdentifier == null || centerEmployeeCollectionIdentifiers.includes(centerEmployeeIdentifier)) {
          return false;
        }
        centerEmployeeCollectionIdentifiers.push(centerEmployeeIdentifier);
        return true;
      });
      return [...centerEmployeesToAdd, ...centerEmployeeCollection];
    }
    return centerEmployeeCollection;
  }

  protected convertDateFromClient(centerEmployee: ICenterEmployee): ICenterEmployee {
    return Object.assign({}, centerEmployee, {
      joiningDate: centerEmployee.joiningDate?.isValid() ? centerEmployee.joiningDate.format(DATE_FORMAT) : undefined,
      releaseDate: centerEmployee.releaseDate?.isValid() ? centerEmployee.releaseDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.joiningDate = res.body.joiningDate ? dayjs(res.body.joiningDate) : undefined;
      res.body.releaseDate = res.body.releaseDate ? dayjs(res.body.releaseDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((centerEmployee: ICenterEmployee) => {
        centerEmployee.joiningDate = centerEmployee.joiningDate ? dayjs(centerEmployee.joiningDate) : undefined;
        centerEmployee.releaseDate = centerEmployee.releaseDate ? dayjs(centerEmployee.releaseDate) : undefined;
      });
    }
    return res;
  }
}

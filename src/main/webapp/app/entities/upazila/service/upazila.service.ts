import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUpazila, getUpazilaIdentifier } from '../upazila.model';

export type EntityResponseType = HttpResponse<IUpazila>;
export type EntityArrayResponseType = HttpResponse<IUpazila[]>;

@Injectable({ providedIn: 'root' })
export class UpazilaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/upazilas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(upazila: IUpazila): Observable<EntityResponseType> {
    return this.http.post<IUpazila>(this.resourceUrl, upazila, { observe: 'response' });
  }

  update(upazila: IUpazila): Observable<EntityResponseType> {
    return this.http.put<IUpazila>(`${this.resourceUrl}/${getUpazilaIdentifier(upazila) as string}`, upazila, { observe: 'response' });
  }

  partialUpdate(upazila: IUpazila): Observable<EntityResponseType> {
    return this.http.patch<IUpazila>(`${this.resourceUrl}/${getUpazilaIdentifier(upazila) as string}`, upazila, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IUpazila>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUpazila[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUpazilaToCollectionIfMissing(upazilaCollection: IUpazila[], ...upazilasToCheck: (IUpazila | null | undefined)[]): IUpazila[] {
    const upazilas: IUpazila[] = upazilasToCheck.filter(isPresent);
    if (upazilas.length > 0) {
      const upazilaCollectionIdentifiers = upazilaCollection.map(upazilaItem => getUpazilaIdentifier(upazilaItem)!);
      const upazilasToAdd = upazilas.filter(upazilaItem => {
        const upazilaIdentifier = getUpazilaIdentifier(upazilaItem);
        if (upazilaIdentifier == null || upazilaCollectionIdentifiers.includes(upazilaIdentifier)) {
          return false;
        }
        upazilaCollectionIdentifiers.push(upazilaIdentifier);
        return true;
      });
      return [...upazilasToAdd, ...upazilaCollection];
    }
    return upazilaCollection;
  }
}

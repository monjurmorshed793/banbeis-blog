import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDivision, getDivisionIdentifier } from '../division.model';

export type EntityResponseType = HttpResponse<IDivision>;
export type EntityArrayResponseType = HttpResponse<IDivision[]>;

@Injectable({ providedIn: 'root' })
export class DivisionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/divisions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(division: IDivision): Observable<EntityResponseType> {
    return this.http.post<IDivision>(this.resourceUrl, division, { observe: 'response' });
  }

  update(division: IDivision): Observable<EntityResponseType> {
    return this.http.put<IDivision>(`${this.resourceUrl}/${getDivisionIdentifier(division) as string}`, division, { observe: 'response' });
  }

  partialUpdate(division: IDivision): Observable<EntityResponseType> {
    return this.http.patch<IDivision>(`${this.resourceUrl}/${getDivisionIdentifier(division) as string}`, division, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IDivision>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDivision[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDivisionToCollectionIfMissing(divisionCollection: IDivision[], ...divisionsToCheck: (IDivision | null | undefined)[]): IDivision[] {
    const divisions: IDivision[] = divisionsToCheck.filter(isPresent);
    if (divisions.length > 0) {
      const divisionCollectionIdentifiers = divisionCollection.map(divisionItem => getDivisionIdentifier(divisionItem)!);
      const divisionsToAdd = divisions.filter(divisionItem => {
        const divisionIdentifier = getDivisionIdentifier(divisionItem);
        if (divisionIdentifier == null || divisionCollectionIdentifiers.includes(divisionIdentifier)) {
          return false;
        }
        divisionCollectionIdentifiers.push(divisionIdentifier);
        return true;
      });
      return [...divisionsToAdd, ...divisionCollection];
    }
    return divisionCollection;
  }
}

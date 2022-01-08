import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICenter, getCenterIdentifier } from '../center.model';

export type EntityResponseType = HttpResponse<ICenter>;
export type EntityArrayResponseType = HttpResponse<ICenter[]>;

@Injectable({ providedIn: 'root' })
export class CenterService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/centers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(center: ICenter): Observable<EntityResponseType> {
    return this.http.post<ICenter>(this.resourceUrl, center, { observe: 'response' });
  }

  update(center: ICenter): Observable<EntityResponseType> {
    return this.http.put<ICenter>(`${this.resourceUrl}/${getCenterIdentifier(center) as string}`, center, { observe: 'response' });
  }

  partialUpdate(center: ICenter): Observable<EntityResponseType> {
    return this.http.patch<ICenter>(`${this.resourceUrl}/${getCenterIdentifier(center) as string}`, center, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ICenter>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICenter[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCenterToCollectionIfMissing(centerCollection: ICenter[], ...centersToCheck: (ICenter | null | undefined)[]): ICenter[] {
    const centers: ICenter[] = centersToCheck.filter(isPresent);
    if (centers.length > 0) {
      const centerCollectionIdentifiers = centerCollection.map(centerItem => getCenterIdentifier(centerItem)!);
      const centersToAdd = centers.filter(centerItem => {
        const centerIdentifier = getCenterIdentifier(centerItem);
        if (centerIdentifier == null || centerCollectionIdentifiers.includes(centerIdentifier)) {
          return false;
        }
        centerCollectionIdentifiers.push(centerIdentifier);
        return true;
      });
      return [...centersToAdd, ...centerCollection];
    }
    return centerCollection;
  }
}

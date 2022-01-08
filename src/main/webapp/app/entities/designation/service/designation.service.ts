import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDesignation, getDesignationIdentifier } from '../designation.model';

export type EntityResponseType = HttpResponse<IDesignation>;
export type EntityArrayResponseType = HttpResponse<IDesignation[]>;

@Injectable({ providedIn: 'root' })
export class DesignationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/designations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(designation: IDesignation): Observable<EntityResponseType> {
    return this.http.post<IDesignation>(this.resourceUrl, designation, { observe: 'response' });
  }

  update(designation: IDesignation): Observable<EntityResponseType> {
    return this.http.put<IDesignation>(`${this.resourceUrl}/${getDesignationIdentifier(designation) as string}`, designation, {
      observe: 'response',
    });
  }

  partialUpdate(designation: IDesignation): Observable<EntityResponseType> {
    return this.http.patch<IDesignation>(`${this.resourceUrl}/${getDesignationIdentifier(designation) as string}`, designation, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IDesignation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDesignation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDesignationToCollectionIfMissing(
    designationCollection: IDesignation[],
    ...designationsToCheck: (IDesignation | null | undefined)[]
  ): IDesignation[] {
    const designations: IDesignation[] = designationsToCheck.filter(isPresent);
    if (designations.length > 0) {
      const designationCollectionIdentifiers = designationCollection.map(designationItem => getDesignationIdentifier(designationItem)!);
      const designationsToAdd = designations.filter(designationItem => {
        const designationIdentifier = getDesignationIdentifier(designationItem);
        if (designationIdentifier == null || designationCollectionIdentifiers.includes(designationIdentifier)) {
          return false;
        }
        designationCollectionIdentifiers.push(designationIdentifier);
        return true;
      });
      return [...designationsToAdd, ...designationCollection];
    }
    return designationCollection;
  }
}

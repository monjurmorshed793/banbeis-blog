import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INavigation, getNavigationIdentifier } from '../navigation.model';

export type EntityResponseType = HttpResponse<INavigation>;
export type EntityArrayResponseType = HttpResponse<INavigation[]>;

@Injectable({ providedIn: 'root' })
export class NavigationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/navigations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(navigation: INavigation): Observable<EntityResponseType> {
    return this.http.post<INavigation>(this.resourceUrl, navigation, { observe: 'response' });
  }

  update(navigation: INavigation): Observable<EntityResponseType> {
    return this.http.put<INavigation>(`${this.resourceUrl}/${getNavigationIdentifier(navigation) as string}`, navigation, {
      observe: 'response',
    });
  }

  partialUpdate(navigation: INavigation): Observable<EntityResponseType> {
    return this.http.patch<INavigation>(`${this.resourceUrl}/${getNavigationIdentifier(navigation) as string}`, navigation, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<INavigation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INavigation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNavigationToCollectionIfMissing(
    navigationCollection: INavigation[],
    ...navigationsToCheck: (INavigation | null | undefined)[]
  ): INavigation[] {
    const navigations: INavigation[] = navigationsToCheck.filter(isPresent);
    if (navigations.length > 0) {
      const navigationCollectionIdentifiers = navigationCollection.map(navigationItem => getNavigationIdentifier(navigationItem)!);
      const navigationsToAdd = navigations.filter(navigationItem => {
        const navigationIdentifier = getNavigationIdentifier(navigationItem);
        if (navigationIdentifier == null || navigationCollectionIdentifiers.includes(navigationIdentifier)) {
          return false;
        }
        navigationCollectionIdentifiers.push(navigationIdentifier);
        return true;
      });
      return [...navigationsToAdd, ...navigationCollection];
    }
    return navigationCollection;
  }
}

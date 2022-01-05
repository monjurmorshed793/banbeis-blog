import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICenterImages, getCenterImagesIdentifier } from '../center-images.model';

export type EntityResponseType = HttpResponse<ICenterImages>;
export type EntityArrayResponseType = HttpResponse<ICenterImages[]>;

@Injectable({ providedIn: 'root' })
export class CenterImagesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/center-images');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(centerImages: ICenterImages): Observable<EntityResponseType> {
    return this.http.post<ICenterImages>(this.resourceUrl, centerImages, { observe: 'response' });
  }

  update(centerImages: ICenterImages): Observable<EntityResponseType> {
    return this.http.put<ICenterImages>(`${this.resourceUrl}/${getCenterImagesIdentifier(centerImages) as string}`, centerImages, {
      observe: 'response',
    });
  }

  partialUpdate(centerImages: ICenterImages): Observable<EntityResponseType> {
    return this.http.patch<ICenterImages>(`${this.resourceUrl}/${getCenterImagesIdentifier(centerImages) as string}`, centerImages, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ICenterImages>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICenterImages[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCenterImagesToCollectionIfMissing(
    centerImagesCollection: ICenterImages[],
    ...centerImagesToCheck: (ICenterImages | null | undefined)[]
  ): ICenterImages[] {
    const centerImages: ICenterImages[] = centerImagesToCheck.filter(isPresent);
    if (centerImages.length > 0) {
      const centerImagesCollectionIdentifiers = centerImagesCollection.map(
        centerImagesItem => getCenterImagesIdentifier(centerImagesItem)!
      );
      const centerImagesToAdd = centerImages.filter(centerImagesItem => {
        const centerImagesIdentifier = getCenterImagesIdentifier(centerImagesItem);
        if (centerImagesIdentifier == null || centerImagesCollectionIdentifiers.includes(centerImagesIdentifier)) {
          return false;
        }
        centerImagesCollectionIdentifiers.push(centerImagesIdentifier);
        return true;
      });
      return [...centerImagesToAdd, ...centerImagesCollection];
    }
    return centerImagesCollection;
  }
}

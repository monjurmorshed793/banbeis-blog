import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPostPhoto, getPostPhotoIdentifier } from '../post-photo.model';

export type EntityResponseType = HttpResponse<IPostPhoto>;
export type EntityArrayResponseType = HttpResponse<IPostPhoto[]>;

@Injectable({ providedIn: 'root' })
export class PostPhotoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/post-photos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(postPhoto: IPostPhoto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(postPhoto);
    return this.http
      .post<IPostPhoto>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(postPhoto: IPostPhoto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(postPhoto);
    return this.http
      .put<IPostPhoto>(`${this.resourceUrl}/${getPostPhotoIdentifier(postPhoto) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(postPhoto: IPostPhoto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(postPhoto);
    return this.http
      .patch<IPostPhoto>(`${this.resourceUrl}/${getPostPhotoIdentifier(postPhoto) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IPostPhoto>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPostPhoto[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPostPhotoToCollectionIfMissing(
    postPhotoCollection: IPostPhoto[],
    ...postPhotosToCheck: (IPostPhoto | null | undefined)[]
  ): IPostPhoto[] {
    const postPhotos: IPostPhoto[] = postPhotosToCheck.filter(isPresent);
    if (postPhotos.length > 0) {
      const postPhotoCollectionIdentifiers = postPhotoCollection.map(postPhotoItem => getPostPhotoIdentifier(postPhotoItem)!);
      const postPhotosToAdd = postPhotos.filter(postPhotoItem => {
        const postPhotoIdentifier = getPostPhotoIdentifier(postPhotoItem);
        if (postPhotoIdentifier == null || postPhotoCollectionIdentifiers.includes(postPhotoIdentifier)) {
          return false;
        }
        postPhotoCollectionIdentifiers.push(postPhotoIdentifier);
        return true;
      });
      return [...postPhotosToAdd, ...postPhotoCollection];
    }
    return postPhotoCollection;
  }

  protected convertDateFromClient(postPhoto: IPostPhoto): IPostPhoto {
    return Object.assign({}, postPhoto, {
      uploadedOn: postPhoto.uploadedOn?.isValid() ? postPhoto.uploadedOn.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.uploadedOn = res.body.uploadedOn ? dayjs(res.body.uploadedOn) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((postPhoto: IPostPhoto) => {
        postPhoto.uploadedOn = postPhoto.uploadedOn ? dayjs(postPhoto.uploadedOn) : undefined;
      });
    }
    return res;
  }
}

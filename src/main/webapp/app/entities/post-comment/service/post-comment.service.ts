import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPostComment, getPostCommentIdentifier } from '../post-comment.model';

export type EntityResponseType = HttpResponse<IPostComment>;
export type EntityArrayResponseType = HttpResponse<IPostComment[]>;

@Injectable({ providedIn: 'root' })
export class PostCommentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/post-comments');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(postComment: IPostComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(postComment);
    return this.http
      .post<IPostComment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(postComment: IPostComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(postComment);
    return this.http
      .put<IPostComment>(`${this.resourceUrl}/${getPostCommentIdentifier(postComment) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(postComment: IPostComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(postComment);
    return this.http
      .patch<IPostComment>(`${this.resourceUrl}/${getPostCommentIdentifier(postComment) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IPostComment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPostComment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPostCommentToCollectionIfMissing(
    postCommentCollection: IPostComment[],
    ...postCommentsToCheck: (IPostComment | null | undefined)[]
  ): IPostComment[] {
    const postComments: IPostComment[] = postCommentsToCheck.filter(isPresent);
    if (postComments.length > 0) {
      const postCommentCollectionIdentifiers = postCommentCollection.map(postCommentItem => getPostCommentIdentifier(postCommentItem)!);
      const postCommentsToAdd = postComments.filter(postCommentItem => {
        const postCommentIdentifier = getPostCommentIdentifier(postCommentItem);
        if (postCommentIdentifier == null || postCommentCollectionIdentifiers.includes(postCommentIdentifier)) {
          return false;
        }
        postCommentCollectionIdentifiers.push(postCommentIdentifier);
        return true;
      });
      return [...postCommentsToAdd, ...postCommentCollection];
    }
    return postCommentCollection;
  }

  protected convertDateFromClient(postComment: IPostComment): IPostComment {
    return Object.assign({}, postComment, {
      commentedOn: postComment.commentedOn?.isValid() ? postComment.commentedOn.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.commentedOn = res.body.commentedOn ? dayjs(res.body.commentedOn) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((postComment: IPostComment) => {
        postComment.commentedOn = postComment.commentedOn ? dayjs(postComment.commentedOn) : undefined;
      });
    }
    return res;
  }
}

import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPostPhoto, PostPhoto } from '../post-photo.model';
import { PostPhotoService } from '../service/post-photo.service';

@Injectable({ providedIn: 'root' })
export class PostPhotoRoutingResolveService implements Resolve<IPostPhoto> {
  constructor(protected service: PostPhotoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPostPhoto> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((postPhoto: HttpResponse<PostPhoto>) => {
          if (postPhoto.body) {
            return of(postPhoto.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PostPhoto());
  }
}

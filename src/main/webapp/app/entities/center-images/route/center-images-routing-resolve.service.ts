import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICenterImages, CenterImages } from '../center-images.model';
import { CenterImagesService } from '../service/center-images.service';

@Injectable({ providedIn: 'root' })
export class CenterImagesRoutingResolveService implements Resolve<ICenterImages> {
  constructor(protected service: CenterImagesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICenterImages> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((centerImages: HttpResponse<CenterImages>) => {
          if (centerImages.body) {
            return of(centerImages.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CenterImages());
  }
}

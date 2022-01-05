import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICenter, Center } from '../center.model';
import { CenterService } from '../service/center.service';

@Injectable({ providedIn: 'root' })
export class CenterRoutingResolveService implements Resolve<ICenter> {
  constructor(protected service: CenterService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICenter> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((center: HttpResponse<Center>) => {
          if (center.body) {
            return of(center.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Center());
  }
}

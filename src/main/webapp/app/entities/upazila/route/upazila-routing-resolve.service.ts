import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUpazila, Upazila } from '../upazila.model';
import { UpazilaService } from '../service/upazila.service';

@Injectable({ providedIn: 'root' })
export class UpazilaRoutingResolveService implements Resolve<IUpazila> {
  constructor(protected service: UpazilaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUpazila> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((upazila: HttpResponse<Upazila>) => {
          if (upazila.body) {
            return of(upazila.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Upazila());
  }
}

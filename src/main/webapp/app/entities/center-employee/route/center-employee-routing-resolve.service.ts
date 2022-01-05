import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICenterEmployee, CenterEmployee } from '../center-employee.model';
import { CenterEmployeeService } from '../service/center-employee.service';

@Injectable({ providedIn: 'root' })
export class CenterEmployeeRoutingResolveService implements Resolve<ICenterEmployee> {
  constructor(protected service: CenterEmployeeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICenterEmployee> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((centerEmployee: HttpResponse<CenterEmployee>) => {
          if (centerEmployee.body) {
            return of(centerEmployee.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CenterEmployee());
  }
}

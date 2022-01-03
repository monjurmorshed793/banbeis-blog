import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INavigation, Navigation } from '../navigation.model';
import { NavigationService } from '../service/navigation.service';

@Injectable({ providedIn: 'root' })
export class NavigationRoutingResolveService implements Resolve<INavigation> {
  constructor(protected service: NavigationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INavigation> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((navigation: HttpResponse<Navigation>) => {
          if (navigation.body) {
            return of(navigation.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Navigation());
  }
}

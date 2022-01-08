import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CenterComponent } from '../list/center.component';
import { CenterDetailComponent } from '../detail/center-detail.component';
import { CenterUpdateComponent } from '../update/center-update.component';
import { CenterRoutingResolveService } from './center-routing-resolve.service';

const centerRoute: Routes = [
  {
    path: '',
    component: CenterComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CenterDetailComponent,
    resolve: {
      center: CenterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CenterUpdateComponent,
    resolve: {
      center: CenterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CenterUpdateComponent,
    resolve: {
      center: CenterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(centerRoute)],
  exports: [RouterModule],
})
export class CenterRoutingModule {}

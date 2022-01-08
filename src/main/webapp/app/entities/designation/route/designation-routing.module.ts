import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DesignationComponent } from '../list/designation.component';
import { DesignationDetailComponent } from '../detail/designation-detail.component';
import { DesignationUpdateComponent } from '../update/designation-update.component';
import { DesignationRoutingResolveService } from './designation-routing-resolve.service';

const designationRoute: Routes = [
  {
    path: '',
    component: DesignationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DesignationDetailComponent,
    resolve: {
      designation: DesignationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DesignationUpdateComponent,
    resolve: {
      designation: DesignationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DesignationUpdateComponent,
    resolve: {
      designation: DesignationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(designationRoute)],
  exports: [RouterModule],
})
export class DesignationRoutingModule {}

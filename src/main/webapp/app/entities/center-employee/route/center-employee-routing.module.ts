import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CenterEmployeeComponent } from '../list/center-employee.component';
import { CenterEmployeeDetailComponent } from '../detail/center-employee-detail.component';
import { CenterEmployeeUpdateComponent } from '../update/center-employee-update.component';
import { CenterEmployeeRoutingResolveService } from './center-employee-routing-resolve.service';

const centerEmployeeRoute: Routes = [
  {
    path: '',
    component: CenterEmployeeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CenterEmployeeDetailComponent,
    resolve: {
      centerEmployee: CenterEmployeeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CenterEmployeeUpdateComponent,
    resolve: {
      centerEmployee: CenterEmployeeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CenterEmployeeUpdateComponent,
    resolve: {
      centerEmployee: CenterEmployeeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(centerEmployeeRoute)],
  exports: [RouterModule],
})
export class CenterEmployeeRoutingModule {}

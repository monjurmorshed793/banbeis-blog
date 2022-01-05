import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UpazilaComponent } from '../list/upazila.component';
import { UpazilaDetailComponent } from '../detail/upazila-detail.component';
import { UpazilaUpdateComponent } from '../update/upazila-update.component';
import { UpazilaRoutingResolveService } from './upazila-routing-resolve.service';

const upazilaRoute: Routes = [
  {
    path: '',
    component: UpazilaComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UpazilaDetailComponent,
    resolve: {
      upazila: UpazilaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UpazilaUpdateComponent,
    resolve: {
      upazila: UpazilaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UpazilaUpdateComponent,
    resolve: {
      upazila: UpazilaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(upazilaRoute)],
  exports: [RouterModule],
})
export class UpazilaRoutingModule {}

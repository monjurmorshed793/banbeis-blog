import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CenterImagesComponent } from '../list/center-images.component';
import { CenterImagesDetailComponent } from '../detail/center-images-detail.component';
import { CenterImagesUpdateComponent } from '../update/center-images-update.component';
import { CenterImagesRoutingResolveService } from './center-images-routing-resolve.service';

const centerImagesRoute: Routes = [
  {
    path: '',
    component: CenterImagesComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CenterImagesDetailComponent,
    resolve: {
      centerImages: CenterImagesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CenterImagesUpdateComponent,
    resolve: {
      centerImages: CenterImagesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CenterImagesUpdateComponent,
    resolve: {
      centerImages: CenterImagesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(centerImagesRoute)],
  exports: [RouterModule],
})
export class CenterImagesRoutingModule {}

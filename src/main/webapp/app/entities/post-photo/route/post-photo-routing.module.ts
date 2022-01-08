import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PostPhotoComponent } from '../list/post-photo.component';
import { PostPhotoDetailComponent } from '../detail/post-photo-detail.component';
import { PostPhotoUpdateComponent } from '../update/post-photo-update.component';
import { PostPhotoRoutingResolveService } from './post-photo-routing-resolve.service';

const postPhotoRoute: Routes = [
  {
    path: '',
    component: PostPhotoComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PostPhotoDetailComponent,
    resolve: {
      postPhoto: PostPhotoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PostPhotoUpdateComponent,
    resolve: {
      postPhoto: PostPhotoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PostPhotoUpdateComponent,
    resolve: {
      postPhoto: PostPhotoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(postPhotoRoute)],
  exports: [RouterModule],
})
export class PostPhotoRoutingModule {}

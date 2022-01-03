import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NavigationComponent } from '../list/navigation.component';
import { NavigationDetailComponent } from '../detail/navigation-detail.component';
import { NavigationUpdateComponent } from '../update/navigation-update.component';
import { NavigationRoutingResolveService } from './navigation-routing-resolve.service';

const navigationRoute: Routes = [
  {
    path: '',
    component: NavigationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NavigationDetailComponent,
    resolve: {
      navigation: NavigationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NavigationUpdateComponent,
    resolve: {
      navigation: NavigationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NavigationUpdateComponent,
    resolve: {
      navigation: NavigationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(navigationRoute)],
  exports: [RouterModule],
})
export class NavigationRoutingModule {}

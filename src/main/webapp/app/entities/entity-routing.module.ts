import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'navigation',
        data: { pageTitle: 'Navigations' },
        loadChildren: () => import('./navigation/navigation.module').then(m => m.NavigationModule),
      },
      {
        path: 'division',
        data: { pageTitle: 'Divisions' },
        loadChildren: () => import('./division/division.module').then(m => m.DivisionModule),
      },
      {
        path: 'district',
        data: { pageTitle: 'Districts' },
        loadChildren: () => import('./district/district.module').then(m => m.DistrictModule),
      },
      {
        path: 'upazila',
        data: { pageTitle: 'Upazilas' },
        loadChildren: () => import('./upazila/upazila.module').then(m => m.UpazilaModule),
      },
      {
        path: 'center',
        data: { pageTitle: 'Centers' },
        loadChildren: () => import('./center/center.module').then(m => m.CenterModule),
      },
      {
        path: 'designation',
        data: { pageTitle: 'Designations' },
        loadChildren: () => import('./designation/designation.module').then(m => m.DesignationModule),
      },
      {
        path: 'employee',
        data: { pageTitle: 'Employees' },
        loadChildren: () => import('./employee/employee.module').then(m => m.EmployeeModule),
      },
      {
        path: 'center-employee',
        data: { pageTitle: 'CenterEmployees' },
        loadChildren: () => import('./center-employee/center-employee.module').then(m => m.CenterEmployeeModule),
      },
      {
        path: 'center-images',
        data: { pageTitle: 'CenterImages' },
        loadChildren: () => import('./center-images/center-images.module').then(m => m.CenterImagesModule),
      },
      {
        path: 'post',
        data: { pageTitle: 'Posts' },
        loadChildren: () => import('./post/post.module').then(m => m.PostModule),
      },
      {
        path: 'post-photo',
        data: { pageTitle: 'PostPhotos' },
        loadChildren: () => import('./post-photo/post-photo.module').then(m => m.PostPhotoModule),
      },
      {
        path: 'post-comment',
        data: { pageTitle: 'PostComments' },
        loadChildren: () => import('./post-comment/post-comment.module').then(m => m.PostCommentModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

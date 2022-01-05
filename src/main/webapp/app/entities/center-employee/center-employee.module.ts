import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CenterEmployeeComponent } from './list/center-employee.component';
import { CenterEmployeeDetailComponent } from './detail/center-employee-detail.component';
import { CenterEmployeeUpdateComponent } from './update/center-employee-update.component';
import { CenterEmployeeDeleteDialogComponent } from './delete/center-employee-delete-dialog.component';
import { CenterEmployeeRoutingModule } from './route/center-employee-routing.module';

@NgModule({
  imports: [SharedModule, CenterEmployeeRoutingModule],
  declarations: [
    CenterEmployeeComponent,
    CenterEmployeeDetailComponent,
    CenterEmployeeUpdateComponent,
    CenterEmployeeDeleteDialogComponent,
  ],
  entryComponents: [CenterEmployeeDeleteDialogComponent],
})
export class CenterEmployeeModule {}

import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UpazilaComponent } from './list/upazila.component';
import { UpazilaDetailComponent } from './detail/upazila-detail.component';
import { UpazilaUpdateComponent } from './update/upazila-update.component';
import { UpazilaDeleteDialogComponent } from './delete/upazila-delete-dialog.component';
import { UpazilaRoutingModule } from './route/upazila-routing.module';

@NgModule({
  imports: [SharedModule, UpazilaRoutingModule],
  declarations: [UpazilaComponent, UpazilaDetailComponent, UpazilaUpdateComponent, UpazilaDeleteDialogComponent],
  entryComponents: [UpazilaDeleteDialogComponent],
})
export class UpazilaModule {}

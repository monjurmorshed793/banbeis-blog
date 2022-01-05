import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CenterComponent } from './list/center.component';
import { CenterDetailComponent } from './detail/center-detail.component';
import { CenterUpdateComponent } from './update/center-update.component';
import { CenterDeleteDialogComponent } from './delete/center-delete-dialog.component';
import { CenterRoutingModule } from './route/center-routing.module';

@NgModule({
  imports: [SharedModule, CenterRoutingModule],
  declarations: [CenterComponent, CenterDetailComponent, CenterUpdateComponent, CenterDeleteDialogComponent],
  entryComponents: [CenterDeleteDialogComponent],
})
export class CenterModule {}

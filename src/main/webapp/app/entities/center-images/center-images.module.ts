import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CenterImagesComponent } from './list/center-images.component';
import { CenterImagesDetailComponent } from './detail/center-images-detail.component';
import { CenterImagesUpdateComponent } from './update/center-images-update.component';
import { CenterImagesDeleteDialogComponent } from './delete/center-images-delete-dialog.component';
import { CenterImagesRoutingModule } from './route/center-images-routing.module';

@NgModule({
  imports: [SharedModule, CenterImagesRoutingModule],
  declarations: [CenterImagesComponent, CenterImagesDetailComponent, CenterImagesUpdateComponent, CenterImagesDeleteDialogComponent],
  entryComponents: [CenterImagesDeleteDialogComponent],
})
export class CenterImagesModule {}

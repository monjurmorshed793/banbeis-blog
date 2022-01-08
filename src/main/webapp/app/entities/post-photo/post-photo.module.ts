import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PostPhotoComponent } from './list/post-photo.component';
import { PostPhotoDetailComponent } from './detail/post-photo-detail.component';
import { PostPhotoUpdateComponent } from './update/post-photo-update.component';
import { PostPhotoDeleteDialogComponent } from './delete/post-photo-delete-dialog.component';
import { PostPhotoRoutingModule } from './route/post-photo-routing.module';

@NgModule({
  imports: [SharedModule, PostPhotoRoutingModule],
  declarations: [PostPhotoComponent, PostPhotoDetailComponent, PostPhotoUpdateComponent, PostPhotoDeleteDialogComponent],
  entryComponents: [PostPhotoDeleteDialogComponent],
})
export class PostPhotoModule {}

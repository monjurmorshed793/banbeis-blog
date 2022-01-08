import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPostPhoto } from '../post-photo.model';
import { PostPhotoService } from '../service/post-photo.service';

@Component({
  templateUrl: './post-photo-delete-dialog.component.html',
})
export class PostPhotoDeleteDialogComponent {
  postPhoto?: IPostPhoto;

  constructor(protected postPhotoService: PostPhotoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.postPhotoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

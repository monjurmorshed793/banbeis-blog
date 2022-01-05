import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICenterImages } from '../center-images.model';
import { CenterImagesService } from '../service/center-images.service';

@Component({
  templateUrl: './center-images-delete-dialog.component.html',
})
export class CenterImagesDeleteDialogComponent {
  centerImages?: ICenterImages;

  constructor(protected centerImagesService: CenterImagesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.centerImagesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

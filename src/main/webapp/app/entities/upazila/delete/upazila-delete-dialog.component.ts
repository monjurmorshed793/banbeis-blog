import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUpazila } from '../upazila.model';
import { UpazilaService } from '../service/upazila.service';

@Component({
  templateUrl: './upazila-delete-dialog.component.html',
})
export class UpazilaDeleteDialogComponent {
  upazila?: IUpazila;

  constructor(protected upazilaService: UpazilaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.upazilaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

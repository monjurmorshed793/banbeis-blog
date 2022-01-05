import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDesignation } from '../designation.model';
import { DesignationService } from '../service/designation.service';

@Component({
  templateUrl: './designation-delete-dialog.component.html',
})
export class DesignationDeleteDialogComponent {
  designation?: IDesignation;

  constructor(protected designationService: DesignationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.designationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

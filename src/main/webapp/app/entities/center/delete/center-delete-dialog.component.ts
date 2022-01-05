import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICenter } from '../center.model';
import { CenterService } from '../service/center.service';

@Component({
  templateUrl: './center-delete-dialog.component.html',
})
export class CenterDeleteDialogComponent {
  center?: ICenter;

  constructor(protected centerService: CenterService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.centerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

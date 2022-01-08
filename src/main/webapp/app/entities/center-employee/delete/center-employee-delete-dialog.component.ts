import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICenterEmployee } from '../center-employee.model';
import { CenterEmployeeService } from '../service/center-employee.service';

@Component({
  templateUrl: './center-employee-delete-dialog.component.html',
})
export class CenterEmployeeDeleteDialogComponent {
  centerEmployee?: ICenterEmployee;

  constructor(protected centerEmployeeService: CenterEmployeeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.centerEmployeeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

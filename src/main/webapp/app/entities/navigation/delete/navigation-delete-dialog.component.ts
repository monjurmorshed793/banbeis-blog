import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INavigation } from '../navigation.model';
import { NavigationService } from '../service/navigation.service';

@Component({
  templateUrl: './navigation-delete-dialog.component.html',
})
export class NavigationDeleteDialogComponent {
  navigation?: INavigation;

  constructor(protected navigationService: NavigationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.navigationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

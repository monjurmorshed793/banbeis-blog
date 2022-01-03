import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NavigationComponent } from './list/navigation.component';
import { NavigationDetailComponent } from './detail/navigation-detail.component';
import { NavigationUpdateComponent } from './update/navigation-update.component';
import { NavigationDeleteDialogComponent } from './delete/navigation-delete-dialog.component';
import { NavigationRoutingModule } from './route/navigation-routing.module';

@NgModule({
  imports: [SharedModule, NavigationRoutingModule],
  declarations: [NavigationComponent, NavigationDetailComponent, NavigationUpdateComponent, NavigationDeleteDialogComponent],
  entryComponents: [NavigationDeleteDialogComponent],
})
export class NavigationModule {}

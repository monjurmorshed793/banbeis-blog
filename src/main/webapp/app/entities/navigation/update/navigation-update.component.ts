import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { INavigation, Navigation } from '../navigation.model';
import { NavigationService } from '../service/navigation.service';

@Component({
  selector: 'jhi-navigation-update',
  templateUrl: './navigation-update.component.html',
})
export class NavigationUpdateComponent implements OnInit {
  isSaving = false;

  navigationsSharedCollection: INavigation[] = [];

  editForm = this.fb.group({
    id: [],
    sequence: [null, []],
    route: [null, [Validators.required]],
    title: [null, [Validators.required]],
    breadCrumb: [],
    parent: [],
  });

  constructor(protected navigationService: NavigationService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ navigation }) => {
      this.updateForm(navigation);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const navigation = this.createFromForm();
    if (navigation.id !== undefined) {
      this.subscribeToSaveResponse(this.navigationService.update(navigation));
    } else {
      this.subscribeToSaveResponse(this.navigationService.create(navigation));
    }
  }

  trackNavigationById(index: number, item: INavigation): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INavigation>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(navigation: INavigation): void {
    this.editForm.patchValue({
      id: navigation.id,
      sequence: navigation.sequence,
      route: navigation.route,
      title: navigation.title,
      breadCrumb: navigation.breadCrumb,
      parent: navigation.parent,
    });

    this.navigationsSharedCollection = this.navigationService.addNavigationToCollectionIfMissing(
      this.navigationsSharedCollection,
      navigation.parent
    );
  }

  protected loadRelationshipsOptions(): void {
    this.navigationService
      .query()
      .pipe(map((res: HttpResponse<INavigation[]>) => res.body ?? []))
      .pipe(
        map((navigations: INavigation[]) =>
          this.navigationService.addNavigationToCollectionIfMissing(navigations, this.editForm.get('parent')!.value)
        )
      )
      .subscribe((navigations: INavigation[]) => (this.navigationsSharedCollection = navigations));
  }

  protected createFromForm(): INavigation {
    return {
      ...new Navigation(),
      id: this.editForm.get(['id'])!.value,
      sequence: this.editForm.get(['sequence'])!.value,
      route: this.editForm.get(['route'])!.value,
      title: this.editForm.get(['title'])!.value,
      breadCrumb: this.editForm.get(['breadCrumb'])!.value,
      parent: this.editForm.get(['parent'])!.value,
    };
  }
}

import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICenterImages, CenterImages } from '../center-images.model';
import { CenterImagesService } from '../service/center-images.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ICenter } from 'app/entities/center/center.model';
import { CenterService } from 'app/entities/center/service/center.service';

@Component({
  selector: 'jhi-center-images-update',
  templateUrl: './center-images-update.component.html',
})
export class CenterImagesUpdateComponent implements OnInit {
  isSaving = false;

  centersSharedCollection: ICenter[] = [];

  editForm = this.fb.group({
    id: [],
    image: [],
    imageContentType: [],
    title: [],
    description: [],
    show: [],
    center: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected centerImagesService: CenterImagesService,
    protected centerService: CenterService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centerImages }) => {
      this.updateForm(centerImages);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('banbeisBlogApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const centerImages = this.createFromForm();
    if (centerImages.id !== undefined) {
      this.subscribeToSaveResponse(this.centerImagesService.update(centerImages));
    } else {
      this.subscribeToSaveResponse(this.centerImagesService.create(centerImages));
    }
  }

  trackCenterById(index: number, item: ICenter): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICenterImages>>): void {
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

  protected updateForm(centerImages: ICenterImages): void {
    this.editForm.patchValue({
      id: centerImages.id,
      image: centerImages.image,
      imageContentType: centerImages.imageContentType,
      title: centerImages.title,
      description: centerImages.description,
      show: centerImages.show,
      center: centerImages.center,
    });

    this.centersSharedCollection = this.centerService.addCenterToCollectionIfMissing(this.centersSharedCollection, centerImages.center);
  }

  protected loadRelationshipsOptions(): void {
    this.centerService
      .query()
      .pipe(map((res: HttpResponse<ICenter[]>) => res.body ?? []))
      .pipe(map((centers: ICenter[]) => this.centerService.addCenterToCollectionIfMissing(centers, this.editForm.get('center')!.value)))
      .subscribe((centers: ICenter[]) => (this.centersSharedCollection = centers));
  }

  protected createFromForm(): ICenterImages {
    return {
      ...new CenterImages(),
      id: this.editForm.get(['id'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      show: this.editForm.get(['show'])!.value,
      center: this.editForm.get(['center'])!.value,
    };
  }
}

import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICenter, Center } from '../center.model';
import { CenterService } from '../service/center.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IDivision } from 'app/entities/division/division.model';
import { DivisionService } from 'app/entities/division/service/division.service';
import { IDistrict } from 'app/entities/district/district.model';
import { DistrictService } from 'app/entities/district/service/district.service';
import { IUpazila } from 'app/entities/upazila/upazila.model';
import { UpazilaService } from 'app/entities/upazila/service/upazila.service';

@Component({
  selector: 'jhi-center-update',
  templateUrl: './center-update.component.html',
})
export class CenterUpdateComponent implements OnInit {
  isSaving = false;

  divisionsSharedCollection: IDivision[] = [];
  districtsSharedCollection: IDistrict[] = [];
  upazilasSharedCollection: IUpazila[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    addressLine: [],
    image: [],
    imageContentType: [],
    division: [],
    district: [],
    upazila: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected centerService: CenterService,
    protected divisionService: DivisionService,
    protected districtService: DistrictService,
    protected upazilaService: UpazilaService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ center }) => {
      this.updateForm(center);

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
    const center = this.createFromForm();
    if (center.id !== undefined) {
      this.subscribeToSaveResponse(this.centerService.update(center));
    } else {
      this.subscribeToSaveResponse(this.centerService.create(center));
    }
  }

  trackDivisionById(index: number, item: IDivision): string {
    return item.id!;
  }

  trackDistrictById(index: number, item: IDistrict): string {
    return item.id!;
  }

  trackUpazilaById(index: number, item: IUpazila): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICenter>>): void {
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

  protected updateForm(center: ICenter): void {
    this.editForm.patchValue({
      id: center.id,
      name: center.name,
      addressLine: center.addressLine,
      image: center.image,
      imageContentType: center.imageContentType,
      division: center.division,
      district: center.district,
      upazila: center.upazila,
    });

    this.divisionsSharedCollection = this.divisionService.addDivisionToCollectionIfMissing(this.divisionsSharedCollection, center.division);
    this.districtsSharedCollection = this.districtService.addDistrictToCollectionIfMissing(this.districtsSharedCollection, center.district);
    this.upazilasSharedCollection = this.upazilaService.addUpazilaToCollectionIfMissing(this.upazilasSharedCollection, center.upazila);
  }

  protected loadRelationshipsOptions(): void {
    this.divisionService
      .query()
      .pipe(map((res: HttpResponse<IDivision[]>) => res.body ?? []))
      .pipe(
        map((divisions: IDivision[]) =>
          this.divisionService.addDivisionToCollectionIfMissing(divisions, this.editForm.get('division')!.value)
        )
      )
      .subscribe((divisions: IDivision[]) => (this.divisionsSharedCollection = divisions));

    this.districtService
      .query()
      .pipe(map((res: HttpResponse<IDistrict[]>) => res.body ?? []))
      .pipe(
        map((districts: IDistrict[]) =>
          this.districtService.addDistrictToCollectionIfMissing(districts, this.editForm.get('district')!.value)
        )
      )
      .subscribe((districts: IDistrict[]) => (this.districtsSharedCollection = districts));

    this.upazilaService
      .query()
      .pipe(map((res: HttpResponse<IUpazila[]>) => res.body ?? []))
      .pipe(
        map((upazilas: IUpazila[]) => this.upazilaService.addUpazilaToCollectionIfMissing(upazilas, this.editForm.get('upazila')!.value))
      )
      .subscribe((upazilas: IUpazila[]) => (this.upazilasSharedCollection = upazilas));
  }

  protected createFromForm(): ICenter {
    return {
      ...new Center(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      addressLine: this.editForm.get(['addressLine'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      division: this.editForm.get(['division'])!.value,
      district: this.editForm.get(['district'])!.value,
      upazila: this.editForm.get(['upazila'])!.value,
    };
  }
}

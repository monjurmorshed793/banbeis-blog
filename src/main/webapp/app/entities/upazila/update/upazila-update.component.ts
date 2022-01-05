import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUpazila, Upazila } from '../upazila.model';
import { UpazilaService } from '../service/upazila.service';
import { IDistrict } from 'app/entities/district/district.model';
import { DistrictService } from 'app/entities/district/service/district.service';

@Component({
  selector: 'jhi-upazila-update',
  templateUrl: './upazila-update.component.html',
})
export class UpazilaUpdateComponent implements OnInit {
  isSaving = false;

  districtsSharedCollection: IDistrict[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    bnName: [],
    url: [],
    district: [],
  });

  constructor(
    protected upazilaService: UpazilaService,
    protected districtService: DistrictService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ upazila }) => {
      this.updateForm(upazila);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const upazila = this.createFromForm();
    if (upazila.id !== undefined) {
      this.subscribeToSaveResponse(this.upazilaService.update(upazila));
    } else {
      this.subscribeToSaveResponse(this.upazilaService.create(upazila));
    }
  }

  trackDistrictById(index: number, item: IDistrict): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUpazila>>): void {
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

  protected updateForm(upazila: IUpazila): void {
    this.editForm.patchValue({
      id: upazila.id,
      name: upazila.name,
      bnName: upazila.bnName,
      url: upazila.url,
      district: upazila.district,
    });

    this.districtsSharedCollection = this.districtService.addDistrictToCollectionIfMissing(
      this.districtsSharedCollection,
      upazila.district
    );
  }

  protected loadRelationshipsOptions(): void {
    this.districtService
      .query()
      .pipe(map((res: HttpResponse<IDistrict[]>) => res.body ?? []))
      .pipe(
        map((districts: IDistrict[]) =>
          this.districtService.addDistrictToCollectionIfMissing(districts, this.editForm.get('district')!.value)
        )
      )
      .subscribe((districts: IDistrict[]) => (this.districtsSharedCollection = districts));
  }

  protected createFromForm(): IUpazila {
    return {
      ...new Upazila(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      bnName: this.editForm.get(['bnName'])!.value,
      url: this.editForm.get(['url'])!.value,
      district: this.editForm.get(['district'])!.value,
    };
  }
}

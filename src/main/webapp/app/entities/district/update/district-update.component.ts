import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IDistrict, District } from '../district.model';
import { DistrictService } from '../service/district.service';

@Component({
  selector: 'jhi-district-update',
  templateUrl: './district-update.component.html',
})
export class DistrictUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    divisionId: [],
    name: [],
    bnName: [],
    lat: [],
    lon: [],
    url: [],
  });

  constructor(protected districtService: DistrictService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ district }) => {
      this.updateForm(district);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const district = this.createFromForm();
    if (district.id !== undefined) {
      this.subscribeToSaveResponse(this.districtService.update(district));
    } else {
      this.subscribeToSaveResponse(this.districtService.create(district));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDistrict>>): void {
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

  protected updateForm(district: IDistrict): void {
    this.editForm.patchValue({
      id: district.id,
      divisionId: district.divisionId,
      name: district.name,
      bnName: district.bnName,
      lat: district.lat,
      lon: district.lon,
      url: district.url,
    });
  }

  protected createFromForm(): IDistrict {
    return {
      ...new District(),
      id: this.editForm.get(['id'])!.value,
      divisionId: this.editForm.get(['divisionId'])!.value,
      name: this.editForm.get(['name'])!.value,
      bnName: this.editForm.get(['bnName'])!.value,
      lat: this.editForm.get(['lat'])!.value,
      lon: this.editForm.get(['lon'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}

import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUpazila, Upazila } from '../upazila.model';
import { UpazilaService } from '../service/upazila.service';

@Component({
  selector: 'jhi-upazila-update',
  templateUrl: './upazila-update.component.html',
})
export class UpazilaUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    districtId: [],
    name: [],
    bnName: [],
    url: [],
  });

  constructor(protected upazilaService: UpazilaService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ upazila }) => {
      this.updateForm(upazila);
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
      districtId: upazila.districtId,
      name: upazila.name,
      bnName: upazila.bnName,
      url: upazila.url,
    });
  }

  protected createFromForm(): IUpazila {
    return {
      ...new Upazila(),
      id: this.editForm.get(['id'])!.value,
      districtId: this.editForm.get(['districtId'])!.value,
      name: this.editForm.get(['name'])!.value,
      bnName: this.editForm.get(['bnName'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}

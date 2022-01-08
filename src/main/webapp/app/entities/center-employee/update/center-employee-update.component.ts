import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICenterEmployee, CenterEmployee } from '../center-employee.model';
import { CenterEmployeeService } from '../service/center-employee.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IDesignation } from 'app/entities/designation/designation.model';
import { DesignationService } from 'app/entities/designation/service/designation.service';
import { DutyType } from 'app/entities/enumerations/duty-type.model';

@Component({
  selector: 'jhi-center-employee-update',
  templateUrl: './center-employee-update.component.html',
})
export class CenterEmployeeUpdateComponent implements OnInit {
  isSaving = false;
  dutyTypeValues = Object.keys(DutyType);

  designationsSharedCollection: IDesignation[] = [];

  editForm = this.fb.group({
    id: [],
    dutyType: [],
    joiningDate: [],
    releaseDate: [],
    message: [],
    designation: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected centerEmployeeService: CenterEmployeeService,
    protected designationService: DesignationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centerEmployee }) => {
      this.updateForm(centerEmployee);

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

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const centerEmployee = this.createFromForm();
    if (centerEmployee.id !== undefined) {
      this.subscribeToSaveResponse(this.centerEmployeeService.update(centerEmployee));
    } else {
      this.subscribeToSaveResponse(this.centerEmployeeService.create(centerEmployee));
    }
  }

  trackDesignationById(index: number, item: IDesignation): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICenterEmployee>>): void {
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

  protected updateForm(centerEmployee: ICenterEmployee): void {
    this.editForm.patchValue({
      id: centerEmployee.id,
      dutyType: centerEmployee.dutyType,
      joiningDate: centerEmployee.joiningDate,
      releaseDate: centerEmployee.releaseDate,
      message: centerEmployee.message,
      designation: centerEmployee.designation,
    });

    this.designationsSharedCollection = this.designationService.addDesignationToCollectionIfMissing(
      this.designationsSharedCollection,
      centerEmployee.designation
    );
  }

  protected loadRelationshipsOptions(): void {
    this.designationService
      .query()
      .pipe(map((res: HttpResponse<IDesignation[]>) => res.body ?? []))
      .pipe(
        map((designations: IDesignation[]) =>
          this.designationService.addDesignationToCollectionIfMissing(designations, this.editForm.get('designation')!.value)
        )
      )
      .subscribe((designations: IDesignation[]) => (this.designationsSharedCollection = designations));
  }

  protected createFromForm(): ICenterEmployee {
    return {
      ...new CenterEmployee(),
      id: this.editForm.get(['id'])!.value,
      dutyType: this.editForm.get(['dutyType'])!.value,
      joiningDate: this.editForm.get(['joiningDate'])!.value,
      releaseDate: this.editForm.get(['releaseDate'])!.value,
      message: this.editForm.get(['message'])!.value,
      designation: this.editForm.get(['designation'])!.value,
    };
  }
}

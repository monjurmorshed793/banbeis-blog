import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPostPhoto, PostPhoto } from '../post-photo.model';
import { PostPhotoService } from '../service/post-photo.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPost } from 'app/entities/post/post.model';
import { PostService } from 'app/entities/post/service/post.service';
import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';

@Component({
  selector: 'jhi-post-photo-update',
  templateUrl: './post-photo-update.component.html',
})
export class PostPhotoUpdateComponent implements OnInit {
  isSaving = false;

  postsSharedCollection: IPost[] = [];
  employeesSharedCollection: IEmployee[] = [];

  editForm = this.fb.group({
    id: [],
    sequence: [],
    title: [],
    description: [],
    image: [],
    imageContentType: [],
    uploadedOn: [],
    post: [],
    uploadedBy: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected postPhotoService: PostPhotoService,
    protected postService: PostService,
    protected employeeService: EmployeeService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ postPhoto }) => {
      if (postPhoto.id === undefined) {
        const today = dayjs().startOf('day');
        postPhoto.uploadedOn = today;
      }

      this.updateForm(postPhoto);

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
    const postPhoto = this.createFromForm();
    if (postPhoto.id !== undefined) {
      this.subscribeToSaveResponse(this.postPhotoService.update(postPhoto));
    } else {
      this.subscribeToSaveResponse(this.postPhotoService.create(postPhoto));
    }
  }

  trackPostById(index: number, item: IPost): string {
    return item.id!;
  }

  trackEmployeeById(index: number, item: IEmployee): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPostPhoto>>): void {
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

  protected updateForm(postPhoto: IPostPhoto): void {
    this.editForm.patchValue({
      id: postPhoto.id,
      sequence: postPhoto.sequence,
      title: postPhoto.title,
      description: postPhoto.description,
      image: postPhoto.image,
      imageContentType: postPhoto.imageContentType,
      uploadedOn: postPhoto.uploadedOn ? postPhoto.uploadedOn.format(DATE_TIME_FORMAT) : null,
      post: postPhoto.post,
      uploadedBy: postPhoto.uploadedBy,
    });

    this.postsSharedCollection = this.postService.addPostToCollectionIfMissing(this.postsSharedCollection, postPhoto.post);
    this.employeesSharedCollection = this.employeeService.addEmployeeToCollectionIfMissing(
      this.employeesSharedCollection,
      postPhoto.uploadedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.postService
      .query()
      .pipe(map((res: HttpResponse<IPost[]>) => res.body ?? []))
      .pipe(map((posts: IPost[]) => this.postService.addPostToCollectionIfMissing(posts, this.editForm.get('post')!.value)))
      .subscribe((posts: IPost[]) => (this.postsSharedCollection = posts));

    this.employeeService
      .query()
      .pipe(map((res: HttpResponse<IEmployee[]>) => res.body ?? []))
      .pipe(
        map((employees: IEmployee[]) =>
          this.employeeService.addEmployeeToCollectionIfMissing(employees, this.editForm.get('uploadedBy')!.value)
        )
      )
      .subscribe((employees: IEmployee[]) => (this.employeesSharedCollection = employees));
  }

  protected createFromForm(): IPostPhoto {
    return {
      ...new PostPhoto(),
      id: this.editForm.get(['id'])!.value,
      sequence: this.editForm.get(['sequence'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      uploadedOn: this.editForm.get(['uploadedOn'])!.value ? dayjs(this.editForm.get(['uploadedOn'])!.value, DATE_TIME_FORMAT) : undefined,
      post: this.editForm.get(['post'])!.value,
      uploadedBy: this.editForm.get(['uploadedBy'])!.value,
    };
  }
}

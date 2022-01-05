import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPostComment, PostComment } from '../post-comment.model';
import { PostCommentService } from '../service/post-comment.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { CommentType } from 'app/entities/enumerations/comment-type.model';

@Component({
  selector: 'jhi-post-comment-update',
  templateUrl: './post-comment-update.component.html',
})
export class PostCommentUpdateComponent implements OnInit {
  isSaving = false;
  commentTypeValues = Object.keys(CommentType);

  editForm = this.fb.group({
    id: [],
    commentedBy: [],
    comment: [],
    commentType: [],
    commentedOn: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected postCommentService: PostCommentService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ postComment }) => {
      if (postComment.id === undefined) {
        const today = dayjs().startOf('day');
        postComment.commentedOn = today;
      }

      this.updateForm(postComment);
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
    const postComment = this.createFromForm();
    if (postComment.id !== undefined) {
      this.subscribeToSaveResponse(this.postCommentService.update(postComment));
    } else {
      this.subscribeToSaveResponse(this.postCommentService.create(postComment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPostComment>>): void {
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

  protected updateForm(postComment: IPostComment): void {
    this.editForm.patchValue({
      id: postComment.id,
      commentedBy: postComment.commentedBy,
      comment: postComment.comment,
      commentType: postComment.commentType,
      commentedOn: postComment.commentedOn ? postComment.commentedOn.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): IPostComment {
    return {
      ...new PostComment(),
      id: this.editForm.get(['id'])!.value,
      commentedBy: this.editForm.get(['commentedBy'])!.value,
      comment: this.editForm.get(['comment'])!.value,
      commentType: this.editForm.get(['commentType'])!.value,
      commentedOn: this.editForm.get(['commentedOn'])!.value
        ? dayjs(this.editForm.get(['commentedOn'])!.value, DATE_TIME_FORMAT)
        : undefined,
    };
  }
}

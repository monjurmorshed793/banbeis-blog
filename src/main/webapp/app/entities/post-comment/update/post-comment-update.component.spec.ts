import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PostCommentService } from '../service/post-comment.service';
import { IPostComment, PostComment } from '../post-comment.model';

import { PostCommentUpdateComponent } from './post-comment-update.component';

describe('PostComment Management Update Component', () => {
  let comp: PostCommentUpdateComponent;
  let fixture: ComponentFixture<PostCommentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let postCommentService: PostCommentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PostCommentUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PostCommentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PostCommentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    postCommentService = TestBed.inject(PostCommentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const postComment: IPostComment = { id: 'CBA' };

      activatedRoute.data = of({ postComment });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(postComment));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PostComment>>();
      const postComment = { id: 'ABC' };
      jest.spyOn(postCommentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postComment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postComment }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(postCommentService.update).toHaveBeenCalledWith(postComment);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PostComment>>();
      const postComment = new PostComment();
      jest.spyOn(postCommentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postComment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postComment }));
      saveSubject.complete();

      // THEN
      expect(postCommentService.create).toHaveBeenCalledWith(postComment);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PostComment>>();
      const postComment = { id: 'ABC' };
      jest.spyOn(postCommentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postComment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(postCommentService.update).toHaveBeenCalledWith(postComment);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

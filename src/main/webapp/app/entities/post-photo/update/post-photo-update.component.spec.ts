import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PostPhotoService } from '../service/post-photo.service';
import { IPostPhoto, PostPhoto } from '../post-photo.model';
import { IPost } from 'app/entities/post/post.model';
import { PostService } from 'app/entities/post/service/post.service';
import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';

import { PostPhotoUpdateComponent } from './post-photo-update.component';

describe('PostPhoto Management Update Component', () => {
  let comp: PostPhotoUpdateComponent;
  let fixture: ComponentFixture<PostPhotoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let postPhotoService: PostPhotoService;
  let postService: PostService;
  let employeeService: EmployeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PostPhotoUpdateComponent],
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
      .overrideTemplate(PostPhotoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PostPhotoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    postPhotoService = TestBed.inject(PostPhotoService);
    postService = TestBed.inject(PostService);
    employeeService = TestBed.inject(EmployeeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Post query and add missing value', () => {
      const postPhoto: IPostPhoto = { id: 'CBA' };
      const post: IPost = { id: '03ea6032-e68f-491a-9b7c-de425b8f6333' };
      postPhoto.post = post;

      const postCollection: IPost[] = [{ id: 'fded50a0-71eb-4cca-8691-a7528f05e699' }];
      jest.spyOn(postService, 'query').mockReturnValue(of(new HttpResponse({ body: postCollection })));
      const additionalPosts = [post];
      const expectedCollection: IPost[] = [...additionalPosts, ...postCollection];
      jest.spyOn(postService, 'addPostToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ postPhoto });
      comp.ngOnInit();

      expect(postService.query).toHaveBeenCalled();
      expect(postService.addPostToCollectionIfMissing).toHaveBeenCalledWith(postCollection, ...additionalPosts);
      expect(comp.postsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Employee query and add missing value', () => {
      const postPhoto: IPostPhoto = { id: 'CBA' };
      const uploadedBy: IEmployee = { id: '5e24fea5-4964-4185-8933-b5e40b3a5183' };
      postPhoto.uploadedBy = uploadedBy;

      const employeeCollection: IEmployee[] = [{ id: 'c42ae085-62cd-4af2-854f-7273b46e61c3' }];
      jest.spyOn(employeeService, 'query').mockReturnValue(of(new HttpResponse({ body: employeeCollection })));
      const additionalEmployees = [uploadedBy];
      const expectedCollection: IEmployee[] = [...additionalEmployees, ...employeeCollection];
      jest.spyOn(employeeService, 'addEmployeeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ postPhoto });
      comp.ngOnInit();

      expect(employeeService.query).toHaveBeenCalled();
      expect(employeeService.addEmployeeToCollectionIfMissing).toHaveBeenCalledWith(employeeCollection, ...additionalEmployees);
      expect(comp.employeesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const postPhoto: IPostPhoto = { id: 'CBA' };
      const post: IPost = { id: '74dd554b-01d6-4077-acf1-15b56de234b7' };
      postPhoto.post = post;
      const uploadedBy: IEmployee = { id: 'd15b77b3-63e1-425b-afb2-6d69603b5e2c' };
      postPhoto.uploadedBy = uploadedBy;

      activatedRoute.data = of({ postPhoto });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(postPhoto));
      expect(comp.postsSharedCollection).toContain(post);
      expect(comp.employeesSharedCollection).toContain(uploadedBy);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PostPhoto>>();
      const postPhoto = { id: 'ABC' };
      jest.spyOn(postPhotoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postPhoto }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(postPhotoService.update).toHaveBeenCalledWith(postPhoto);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PostPhoto>>();
      const postPhoto = new PostPhoto();
      jest.spyOn(postPhotoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postPhoto }));
      saveSubject.complete();

      // THEN
      expect(postPhotoService.create).toHaveBeenCalledWith(postPhoto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PostPhoto>>();
      const postPhoto = { id: 'ABC' };
      jest.spyOn(postPhotoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postPhoto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(postPhotoService.update).toHaveBeenCalledWith(postPhoto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPostById', () => {
      it('Should return tracked Post primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackPostById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackEmployeeById', () => {
      it('Should return tracked Employee primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackEmployeeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});

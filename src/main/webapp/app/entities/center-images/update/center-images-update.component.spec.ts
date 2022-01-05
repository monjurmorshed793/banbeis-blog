import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CenterImagesService } from '../service/center-images.service';
import { ICenterImages, CenterImages } from '../center-images.model';
import { ICenter } from 'app/entities/center/center.model';
import { CenterService } from 'app/entities/center/service/center.service';

import { CenterImagesUpdateComponent } from './center-images-update.component';

describe('CenterImages Management Update Component', () => {
  let comp: CenterImagesUpdateComponent;
  let fixture: ComponentFixture<CenterImagesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let centerImagesService: CenterImagesService;
  let centerService: CenterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CenterImagesUpdateComponent],
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
      .overrideTemplate(CenterImagesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CenterImagesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    centerImagesService = TestBed.inject(CenterImagesService);
    centerService = TestBed.inject(CenterService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Center query and add missing value', () => {
      const centerImages: ICenterImages = { id: 'CBA' };
      const center: ICenter = { id: '40268e34-805e-4e2d-9b88-6b3ea4d42abd' };
      centerImages.center = center;

      const centerCollection: ICenter[] = [{ id: '26e08a78-85ca-45cd-ba10-3e05bdca56cd' }];
      jest.spyOn(centerService, 'query').mockReturnValue(of(new HttpResponse({ body: centerCollection })));
      const additionalCenters = [center];
      const expectedCollection: ICenter[] = [...additionalCenters, ...centerCollection];
      jest.spyOn(centerService, 'addCenterToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ centerImages });
      comp.ngOnInit();

      expect(centerService.query).toHaveBeenCalled();
      expect(centerService.addCenterToCollectionIfMissing).toHaveBeenCalledWith(centerCollection, ...additionalCenters);
      expect(comp.centersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const centerImages: ICenterImages = { id: 'CBA' };
      const center: ICenter = { id: '4140e3bb-80c5-4b7f-a5e8-131f3215a2fb' };
      centerImages.center = center;

      activatedRoute.data = of({ centerImages });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(centerImages));
      expect(comp.centersSharedCollection).toContain(center);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CenterImages>>();
      const centerImages = { id: 'ABC' };
      jest.spyOn(centerImagesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centerImages });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centerImages }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(centerImagesService.update).toHaveBeenCalledWith(centerImages);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CenterImages>>();
      const centerImages = new CenterImages();
      jest.spyOn(centerImagesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centerImages });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centerImages }));
      saveSubject.complete();

      // THEN
      expect(centerImagesService.create).toHaveBeenCalledWith(centerImages);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CenterImages>>();
      const centerImages = { id: 'ABC' };
      jest.spyOn(centerImagesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centerImages });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(centerImagesService.update).toHaveBeenCalledWith(centerImages);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCenterById', () => {
      it('Should return tracked Center primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCenterById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});

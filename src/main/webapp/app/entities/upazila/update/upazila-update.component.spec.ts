import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UpazilaService } from '../service/upazila.service';
import { IUpazila, Upazila } from '../upazila.model';
import { IDistrict } from 'app/entities/district/district.model';
import { DistrictService } from 'app/entities/district/service/district.service';

import { UpazilaUpdateComponent } from './upazila-update.component';

describe('Upazila Management Update Component', () => {
  let comp: UpazilaUpdateComponent;
  let fixture: ComponentFixture<UpazilaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let upazilaService: UpazilaService;
  let districtService: DistrictService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UpazilaUpdateComponent],
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
      .overrideTemplate(UpazilaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UpazilaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    upazilaService = TestBed.inject(UpazilaService);
    districtService = TestBed.inject(DistrictService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call District query and add missing value', () => {
      const upazila: IUpazila = { id: 'CBA' };
      const district: IDistrict = { id: '6ab9b46f-d061-49f8-a237-18a28c5738aa' };
      upazila.district = district;

      const districtCollection: IDistrict[] = [{ id: '5c8b1f38-d77b-4f52-86a2-5cdde99cee86' }];
      jest.spyOn(districtService, 'query').mockReturnValue(of(new HttpResponse({ body: districtCollection })));
      const additionalDistricts = [district];
      const expectedCollection: IDistrict[] = [...additionalDistricts, ...districtCollection];
      jest.spyOn(districtService, 'addDistrictToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ upazila });
      comp.ngOnInit();

      expect(districtService.query).toHaveBeenCalled();
      expect(districtService.addDistrictToCollectionIfMissing).toHaveBeenCalledWith(districtCollection, ...additionalDistricts);
      expect(comp.districtsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const upazila: IUpazila = { id: 'CBA' };
      const district: IDistrict = { id: '298da5cc-afda-44a5-abce-6b06f03cf4f1' };
      upazila.district = district;

      activatedRoute.data = of({ upazila });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(upazila));
      expect(comp.districtsSharedCollection).toContain(district);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Upazila>>();
      const upazila = { id: 'ABC' };
      jest.spyOn(upazilaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ upazila });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: upazila }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(upazilaService.update).toHaveBeenCalledWith(upazila);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Upazila>>();
      const upazila = new Upazila();
      jest.spyOn(upazilaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ upazila });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: upazila }));
      saveSubject.complete();

      // THEN
      expect(upazilaService.create).toHaveBeenCalledWith(upazila);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Upazila>>();
      const upazila = { id: 'ABC' };
      jest.spyOn(upazilaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ upazila });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(upazilaService.update).toHaveBeenCalledWith(upazila);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackDistrictById', () => {
      it('Should return tracked District primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackDistrictById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});

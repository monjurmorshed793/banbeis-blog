import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CenterService } from '../service/center.service';
import { ICenter, Center } from '../center.model';
import { IDivision } from 'app/entities/division/division.model';
import { DivisionService } from 'app/entities/division/service/division.service';
import { IDistrict } from 'app/entities/district/district.model';
import { DistrictService } from 'app/entities/district/service/district.service';
import { IUpazila } from 'app/entities/upazila/upazila.model';
import { UpazilaService } from 'app/entities/upazila/service/upazila.service';

import { CenterUpdateComponent } from './center-update.component';

describe('Center Management Update Component', () => {
  let comp: CenterUpdateComponent;
  let fixture: ComponentFixture<CenterUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let centerService: CenterService;
  let divisionService: DivisionService;
  let districtService: DistrictService;
  let upazilaService: UpazilaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CenterUpdateComponent],
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
      .overrideTemplate(CenterUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CenterUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    centerService = TestBed.inject(CenterService);
    divisionService = TestBed.inject(DivisionService);
    districtService = TestBed.inject(DistrictService);
    upazilaService = TestBed.inject(UpazilaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Division query and add missing value', () => {
      const center: ICenter = { id: 'CBA' };
      const division: IDivision = { id: '05aa598d-eb8f-40d8-85f6-119eb639167c' };
      center.division = division;

      const divisionCollection: IDivision[] = [{ id: '91550c0c-62e7-41ff-8272-dea45c75bb8a' }];
      jest.spyOn(divisionService, 'query').mockReturnValue(of(new HttpResponse({ body: divisionCollection })));
      const additionalDivisions = [division];
      const expectedCollection: IDivision[] = [...additionalDivisions, ...divisionCollection];
      jest.spyOn(divisionService, 'addDivisionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ center });
      comp.ngOnInit();

      expect(divisionService.query).toHaveBeenCalled();
      expect(divisionService.addDivisionToCollectionIfMissing).toHaveBeenCalledWith(divisionCollection, ...additionalDivisions);
      expect(comp.divisionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call District query and add missing value', () => {
      const center: ICenter = { id: 'CBA' };
      const district: IDistrict = { id: '7a35b9e4-8dc7-4791-a044-1906fea11c68' };
      center.district = district;

      const districtCollection: IDistrict[] = [{ id: '02f6816e-f8b3-4597-9a72-caa6d246b538' }];
      jest.spyOn(districtService, 'query').mockReturnValue(of(new HttpResponse({ body: districtCollection })));
      const additionalDistricts = [district];
      const expectedCollection: IDistrict[] = [...additionalDistricts, ...districtCollection];
      jest.spyOn(districtService, 'addDistrictToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ center });
      comp.ngOnInit();

      expect(districtService.query).toHaveBeenCalled();
      expect(districtService.addDistrictToCollectionIfMissing).toHaveBeenCalledWith(districtCollection, ...additionalDistricts);
      expect(comp.districtsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Upazila query and add missing value', () => {
      const center: ICenter = { id: 'CBA' };
      const upazila: IUpazila = { id: 'f23a06e4-b401-424b-958c-1f24f7038fd7' };
      center.upazila = upazila;

      const upazilaCollection: IUpazila[] = [{ id: '95a0748c-4016-4d72-b6bd-fcce6677fc6e' }];
      jest.spyOn(upazilaService, 'query').mockReturnValue(of(new HttpResponse({ body: upazilaCollection })));
      const additionalUpazilas = [upazila];
      const expectedCollection: IUpazila[] = [...additionalUpazilas, ...upazilaCollection];
      jest.spyOn(upazilaService, 'addUpazilaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ center });
      comp.ngOnInit();

      expect(upazilaService.query).toHaveBeenCalled();
      expect(upazilaService.addUpazilaToCollectionIfMissing).toHaveBeenCalledWith(upazilaCollection, ...additionalUpazilas);
      expect(comp.upazilasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const center: ICenter = { id: 'CBA' };
      const division: IDivision = { id: '37e82c51-42d6-40f9-b162-47512055947c' };
      center.division = division;
      const district: IDistrict = { id: 'b76ec68e-84e6-49fc-a8be-fc36174b4cba' };
      center.district = district;
      const upazila: IUpazila = { id: '5ad0e508-9c82-4862-b9a5-5a52ee2d8547' };
      center.upazila = upazila;

      activatedRoute.data = of({ center });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(center));
      expect(comp.divisionsSharedCollection).toContain(division);
      expect(comp.districtsSharedCollection).toContain(district);
      expect(comp.upazilasSharedCollection).toContain(upazila);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Center>>();
      const center = { id: 'ABC' };
      jest.spyOn(centerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ center });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: center }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(centerService.update).toHaveBeenCalledWith(center);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Center>>();
      const center = new Center();
      jest.spyOn(centerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ center });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: center }));
      saveSubject.complete();

      // THEN
      expect(centerService.create).toHaveBeenCalledWith(center);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Center>>();
      const center = { id: 'ABC' };
      jest.spyOn(centerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ center });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(centerService.update).toHaveBeenCalledWith(center);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackDivisionById', () => {
      it('Should return tracked Division primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackDivisionById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackDistrictById', () => {
      it('Should return tracked District primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackDistrictById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackUpazilaById', () => {
      it('Should return tracked Upazila primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackUpazilaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});

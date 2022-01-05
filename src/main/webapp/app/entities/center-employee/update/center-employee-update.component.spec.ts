import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CenterEmployeeService } from '../service/center-employee.service';
import { ICenterEmployee, CenterEmployee } from '../center-employee.model';
import { IDesignation } from 'app/entities/designation/designation.model';
import { DesignationService } from 'app/entities/designation/service/designation.service';

import { CenterEmployeeUpdateComponent } from './center-employee-update.component';

describe('CenterEmployee Management Update Component', () => {
  let comp: CenterEmployeeUpdateComponent;
  let fixture: ComponentFixture<CenterEmployeeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let centerEmployeeService: CenterEmployeeService;
  let designationService: DesignationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CenterEmployeeUpdateComponent],
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
      .overrideTemplate(CenterEmployeeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CenterEmployeeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    centerEmployeeService = TestBed.inject(CenterEmployeeService);
    designationService = TestBed.inject(DesignationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Designation query and add missing value', () => {
      const centerEmployee: ICenterEmployee = { id: 'CBA' };
      const designation: IDesignation = { id: '3bd23946-4ab7-4b02-8d28-4d3af17c7e01' };
      centerEmployee.designation = designation;

      const designationCollection: IDesignation[] = [{ id: '57aeed7f-2cba-45fb-aceb-88db68666a95' }];
      jest.spyOn(designationService, 'query').mockReturnValue(of(new HttpResponse({ body: designationCollection })));
      const additionalDesignations = [designation];
      const expectedCollection: IDesignation[] = [...additionalDesignations, ...designationCollection];
      jest.spyOn(designationService, 'addDesignationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ centerEmployee });
      comp.ngOnInit();

      expect(designationService.query).toHaveBeenCalled();
      expect(designationService.addDesignationToCollectionIfMissing).toHaveBeenCalledWith(designationCollection, ...additionalDesignations);
      expect(comp.designationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const centerEmployee: ICenterEmployee = { id: 'CBA' };
      const designation: IDesignation = { id: '42746b8d-235c-4a87-975a-47b4ced4818a' };
      centerEmployee.designation = designation;

      activatedRoute.data = of({ centerEmployee });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(centerEmployee));
      expect(comp.designationsSharedCollection).toContain(designation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CenterEmployee>>();
      const centerEmployee = { id: 'ABC' };
      jest.spyOn(centerEmployeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centerEmployee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centerEmployee }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(centerEmployeeService.update).toHaveBeenCalledWith(centerEmployee);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CenterEmployee>>();
      const centerEmployee = new CenterEmployee();
      jest.spyOn(centerEmployeeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centerEmployee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: centerEmployee }));
      saveSubject.complete();

      // THEN
      expect(centerEmployeeService.create).toHaveBeenCalledWith(centerEmployee);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CenterEmployee>>();
      const centerEmployee = { id: 'ABC' };
      jest.spyOn(centerEmployeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ centerEmployee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(centerEmployeeService.update).toHaveBeenCalledWith(centerEmployee);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackDesignationById', () => {
      it('Should return tracked Designation primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackDesignationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});

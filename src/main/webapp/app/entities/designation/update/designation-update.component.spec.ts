import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DesignationService } from '../service/designation.service';
import { IDesignation, Designation } from '../designation.model';

import { DesignationUpdateComponent } from './designation-update.component';

describe('Designation Management Update Component', () => {
  let comp: DesignationUpdateComponent;
  let fixture: ComponentFixture<DesignationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let designationService: DesignationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DesignationUpdateComponent],
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
      .overrideTemplate(DesignationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DesignationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    designationService = TestBed.inject(DesignationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const designation: IDesignation = { id: 'CBA' };

      activatedRoute.data = of({ designation });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(designation));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Designation>>();
      const designation = { id: 'ABC' };
      jest.spyOn(designationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ designation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: designation }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(designationService.update).toHaveBeenCalledWith(designation);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Designation>>();
      const designation = new Designation();
      jest.spyOn(designationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ designation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: designation }));
      saveSubject.complete();

      // THEN
      expect(designationService.create).toHaveBeenCalledWith(designation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Designation>>();
      const designation = { id: 'ABC' };
      jest.spyOn(designationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ designation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(designationService.update).toHaveBeenCalledWith(designation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

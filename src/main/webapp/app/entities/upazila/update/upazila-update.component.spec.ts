import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UpazilaService } from '../service/upazila.service';
import { IUpazila, Upazila } from '../upazila.model';

import { UpazilaUpdateComponent } from './upazila-update.component';

describe('Upazila Management Update Component', () => {
  let comp: UpazilaUpdateComponent;
  let fixture: ComponentFixture<UpazilaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let upazilaService: UpazilaService;

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

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const upazila: IUpazila = { id: 'CBA' };

      activatedRoute.data = of({ upazila });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(upazila));
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
});

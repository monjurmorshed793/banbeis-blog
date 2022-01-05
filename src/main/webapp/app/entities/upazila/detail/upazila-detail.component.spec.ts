import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UpazilaDetailComponent } from './upazila-detail.component';

describe('Upazila Management Detail Component', () => {
  let comp: UpazilaDetailComponent;
  let fixture: ComponentFixture<UpazilaDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpazilaDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ upazila: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(UpazilaDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UpazilaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load upazila on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.upazila).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});

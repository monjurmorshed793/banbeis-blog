import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DesignationDetailComponent } from './designation-detail.component';

describe('Designation Management Detail Component', () => {
  let comp: DesignationDetailComponent;
  let fixture: ComponentFixture<DesignationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DesignationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ designation: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(DesignationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DesignationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load designation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.designation).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});

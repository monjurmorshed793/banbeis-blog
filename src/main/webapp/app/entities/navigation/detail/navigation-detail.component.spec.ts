import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NavigationDetailComponent } from './navigation-detail.component';

describe('Navigation Management Detail Component', () => {
  let comp: NavigationDetailComponent;
  let fixture: ComponentFixture<NavigationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NavigationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ navigation: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(NavigationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NavigationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load navigation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.navigation).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INavigation, Navigation } from '../navigation.model';

import { NavigationService } from './navigation.service';

describe('Navigation Service', () => {
  let service: NavigationService;
  let httpMock: HttpTestingController;
  let elemDefault: INavigation;
  let expectedResult: INavigation | INavigation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NavigationService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      sequence: 0,
      route: 'AAAAAAA',
      title: 'AAAAAAA',
      breadCrumb: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Navigation', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Navigation()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Navigation', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          sequence: 1,
          route: 'BBBBBB',
          title: 'BBBBBB',
          breadCrumb: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Navigation', () => {
      const patchObject = Object.assign(
        {
          route: 'BBBBBB',
        },
        new Navigation()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Navigation', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          sequence: 1,
          route: 'BBBBBB',
          title: 'BBBBBB',
          breadCrumb: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Navigation', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNavigationToCollectionIfMissing', () => {
      it('should add a Navigation to an empty array', () => {
        const navigation: INavigation = { id: 'ABC' };
        expectedResult = service.addNavigationToCollectionIfMissing([], navigation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(navigation);
      });

      it('should not add a Navigation to an array that contains it', () => {
        const navigation: INavigation = { id: 'ABC' };
        const navigationCollection: INavigation[] = [
          {
            ...navigation,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addNavigationToCollectionIfMissing(navigationCollection, navigation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Navigation to an array that doesn't contain it", () => {
        const navigation: INavigation = { id: 'ABC' };
        const navigationCollection: INavigation[] = [{ id: 'CBA' }];
        expectedResult = service.addNavigationToCollectionIfMissing(navigationCollection, navigation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(navigation);
      });

      it('should add only unique Navigation to an array', () => {
        const navigationArray: INavigation[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '67d63b20-8b7a-4ebf-9792-b20436b2e454' }];
        const navigationCollection: INavigation[] = [{ id: 'ABC' }];
        expectedResult = service.addNavigationToCollectionIfMissing(navigationCollection, ...navigationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const navigation: INavigation = { id: 'ABC' };
        const navigation2: INavigation = { id: 'CBA' };
        expectedResult = service.addNavigationToCollectionIfMissing([], navigation, navigation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(navigation);
        expect(expectedResult).toContain(navigation2);
      });

      it('should accept null and undefined values', () => {
        const navigation: INavigation = { id: 'ABC' };
        expectedResult = service.addNavigationToCollectionIfMissing([], null, navigation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(navigation);
      });

      it('should return initial array if no Navigation is added', () => {
        const navigationCollection: INavigation[] = [{ id: 'ABC' }];
        expectedResult = service.addNavigationToCollectionIfMissing(navigationCollection, undefined, null);
        expect(expectedResult).toEqual(navigationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUpazila, Upazila } from '../upazila.model';

import { UpazilaService } from './upazila.service';

describe('Upazila Service', () => {
  let service: UpazilaService;
  let httpMock: HttpTestingController;
  let elemDefault: IUpazila;
  let expectedResult: IUpazila | IUpazila[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UpazilaService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      districtId: 'AAAAAAA',
      name: 'AAAAAAA',
      bnName: 'AAAAAAA',
      url: 'AAAAAAA',
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

    it('should create a Upazila', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Upazila()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Upazila', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          districtId: 'BBBBBB',
          name: 'BBBBBB',
          bnName: 'BBBBBB',
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Upazila', () => {
      const patchObject = Object.assign(
        {
          districtId: 'BBBBBB',
          bnName: 'BBBBBB',
        },
        new Upazila()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Upazila', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          districtId: 'BBBBBB',
          name: 'BBBBBB',
          bnName: 'BBBBBB',
          url: 'BBBBBB',
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

    it('should delete a Upazila', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUpazilaToCollectionIfMissing', () => {
      it('should add a Upazila to an empty array', () => {
        const upazila: IUpazila = { id: 'ABC' };
        expectedResult = service.addUpazilaToCollectionIfMissing([], upazila);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(upazila);
      });

      it('should not add a Upazila to an array that contains it', () => {
        const upazila: IUpazila = { id: 'ABC' };
        const upazilaCollection: IUpazila[] = [
          {
            ...upazila,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addUpazilaToCollectionIfMissing(upazilaCollection, upazila);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Upazila to an array that doesn't contain it", () => {
        const upazila: IUpazila = { id: 'ABC' };
        const upazilaCollection: IUpazila[] = [{ id: 'CBA' }];
        expectedResult = service.addUpazilaToCollectionIfMissing(upazilaCollection, upazila);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(upazila);
      });

      it('should add only unique Upazila to an array', () => {
        const upazilaArray: IUpazila[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '0a8ad5c2-6e84-40a7-89df-cf63fd6e2d1f' }];
        const upazilaCollection: IUpazila[] = [{ id: 'ABC' }];
        expectedResult = service.addUpazilaToCollectionIfMissing(upazilaCollection, ...upazilaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const upazila: IUpazila = { id: 'ABC' };
        const upazila2: IUpazila = { id: 'CBA' };
        expectedResult = service.addUpazilaToCollectionIfMissing([], upazila, upazila2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(upazila);
        expect(expectedResult).toContain(upazila2);
      });

      it('should accept null and undefined values', () => {
        const upazila: IUpazila = { id: 'ABC' };
        expectedResult = service.addUpazilaToCollectionIfMissing([], null, upazila, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(upazila);
      });

      it('should return initial array if no Upazila is added', () => {
        const upazilaCollection: IUpazila[] = [{ id: 'ABC' }];
        expectedResult = service.addUpazilaToCollectionIfMissing(upazilaCollection, undefined, null);
        expect(expectedResult).toEqual(upazilaCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDivision, Division } from '../division.model';

import { DivisionService } from './division.service';

describe('Division Service', () => {
  let service: DivisionService;
  let httpMock: HttpTestingController;
  let elemDefault: IDivision;
  let expectedResult: IDivision | IDivision[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DivisionService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
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

    it('should create a Division', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Division()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Division', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
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

    it('should partial update a Division', () => {
      const patchObject = Object.assign(
        {
          url: 'BBBBBB',
        },
        new Division()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Division', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
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

    it('should delete a Division', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDivisionToCollectionIfMissing', () => {
      it('should add a Division to an empty array', () => {
        const division: IDivision = { id: 'ABC' };
        expectedResult = service.addDivisionToCollectionIfMissing([], division);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(division);
      });

      it('should not add a Division to an array that contains it', () => {
        const division: IDivision = { id: 'ABC' };
        const divisionCollection: IDivision[] = [
          {
            ...division,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addDivisionToCollectionIfMissing(divisionCollection, division);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Division to an array that doesn't contain it", () => {
        const division: IDivision = { id: 'ABC' };
        const divisionCollection: IDivision[] = [{ id: 'CBA' }];
        expectedResult = service.addDivisionToCollectionIfMissing(divisionCollection, division);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(division);
      });

      it('should add only unique Division to an array', () => {
        const divisionArray: IDivision[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'b076d1cd-f154-4d16-9f1f-ec793b77b515' }];
        const divisionCollection: IDivision[] = [{ id: 'ABC' }];
        expectedResult = service.addDivisionToCollectionIfMissing(divisionCollection, ...divisionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const division: IDivision = { id: 'ABC' };
        const division2: IDivision = { id: 'CBA' };
        expectedResult = service.addDivisionToCollectionIfMissing([], division, division2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(division);
        expect(expectedResult).toContain(division2);
      });

      it('should accept null and undefined values', () => {
        const division: IDivision = { id: 'ABC' };
        expectedResult = service.addDivisionToCollectionIfMissing([], null, division, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(division);
      });

      it('should return initial array if no Division is added', () => {
        const divisionCollection: IDivision[] = [{ id: 'ABC' }];
        expectedResult = service.addDivisionToCollectionIfMissing(divisionCollection, undefined, null);
        expect(expectedResult).toEqual(divisionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

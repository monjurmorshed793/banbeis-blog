import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDesignation, Designation } from '../designation.model';

import { DesignationService } from './designation.service';

describe('Designation Service', () => {
  let service: DesignationService;
  let httpMock: HttpTestingController;
  let elemDefault: IDesignation;
  let expectedResult: IDesignation | IDesignation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DesignationService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      sortName: 'AAAAAAA',
      grade: 0,
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

    it('should create a Designation', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Designation()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Designation', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          sortName: 'BBBBBB',
          grade: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Designation', () => {
      const patchObject = Object.assign({}, new Designation());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Designation', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          sortName: 'BBBBBB',
          grade: 1,
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

    it('should delete a Designation', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDesignationToCollectionIfMissing', () => {
      it('should add a Designation to an empty array', () => {
        const designation: IDesignation = { id: 'ABC' };
        expectedResult = service.addDesignationToCollectionIfMissing([], designation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(designation);
      });

      it('should not add a Designation to an array that contains it', () => {
        const designation: IDesignation = { id: 'ABC' };
        const designationCollection: IDesignation[] = [
          {
            ...designation,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addDesignationToCollectionIfMissing(designationCollection, designation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Designation to an array that doesn't contain it", () => {
        const designation: IDesignation = { id: 'ABC' };
        const designationCollection: IDesignation[] = [{ id: 'CBA' }];
        expectedResult = service.addDesignationToCollectionIfMissing(designationCollection, designation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(designation);
      });

      it('should add only unique Designation to an array', () => {
        const designationArray: IDesignation[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'b3bb271c-9101-4f50-9b21-a69ec4818658' }];
        const designationCollection: IDesignation[] = [{ id: 'ABC' }];
        expectedResult = service.addDesignationToCollectionIfMissing(designationCollection, ...designationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const designation: IDesignation = { id: 'ABC' };
        const designation2: IDesignation = { id: 'CBA' };
        expectedResult = service.addDesignationToCollectionIfMissing([], designation, designation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(designation);
        expect(expectedResult).toContain(designation2);
      });

      it('should accept null and undefined values', () => {
        const designation: IDesignation = { id: 'ABC' };
        expectedResult = service.addDesignationToCollectionIfMissing([], null, designation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(designation);
      });

      it('should return initial array if no Designation is added', () => {
        const designationCollection: IDesignation[] = [{ id: 'ABC' }];
        expectedResult = service.addDesignationToCollectionIfMissing(designationCollection, undefined, null);
        expect(expectedResult).toEqual(designationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

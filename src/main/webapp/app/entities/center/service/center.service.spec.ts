import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICenter, Center } from '../center.model';

import { CenterService } from './center.service';

describe('Center Service', () => {
  let service: CenterService;
  let httpMock: HttpTestingController;
  let elemDefault: ICenter;
  let expectedResult: ICenter | ICenter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CenterService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      addressLine: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
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

    it('should create a Center', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Center()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Center', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          addressLine: 'BBBBBB',
          image: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Center', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          addressLine: 'BBBBBB',
        },
        new Center()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Center', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          addressLine: 'BBBBBB',
          image: 'BBBBBB',
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

    it('should delete a Center', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCenterToCollectionIfMissing', () => {
      it('should add a Center to an empty array', () => {
        const center: ICenter = { id: 'ABC' };
        expectedResult = service.addCenterToCollectionIfMissing([], center);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(center);
      });

      it('should not add a Center to an array that contains it', () => {
        const center: ICenter = { id: 'ABC' };
        const centerCollection: ICenter[] = [
          {
            ...center,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCenterToCollectionIfMissing(centerCollection, center);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Center to an array that doesn't contain it", () => {
        const center: ICenter = { id: 'ABC' };
        const centerCollection: ICenter[] = [{ id: 'CBA' }];
        expectedResult = service.addCenterToCollectionIfMissing(centerCollection, center);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(center);
      });

      it('should add only unique Center to an array', () => {
        const centerArray: ICenter[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '77345534-0b9f-4131-baa9-a1f006486d98' }];
        const centerCollection: ICenter[] = [{ id: 'ABC' }];
        expectedResult = service.addCenterToCollectionIfMissing(centerCollection, ...centerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const center: ICenter = { id: 'ABC' };
        const center2: ICenter = { id: 'CBA' };
        expectedResult = service.addCenterToCollectionIfMissing([], center, center2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(center);
        expect(expectedResult).toContain(center2);
      });

      it('should accept null and undefined values', () => {
        const center: ICenter = { id: 'ABC' };
        expectedResult = service.addCenterToCollectionIfMissing([], null, center, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(center);
      });

      it('should return initial array if no Center is added', () => {
        const centerCollection: ICenter[] = [{ id: 'ABC' }];
        expectedResult = service.addCenterToCollectionIfMissing(centerCollection, undefined, null);
        expect(expectedResult).toEqual(centerCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

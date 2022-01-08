import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { DutyType } from 'app/entities/enumerations/duty-type.model';
import { ICenterEmployee, CenterEmployee } from '../center-employee.model';

import { CenterEmployeeService } from './center-employee.service';

describe('CenterEmployee Service', () => {
  let service: CenterEmployeeService;
  let httpMock: HttpTestingController;
  let elemDefault: ICenterEmployee;
  let expectedResult: ICenterEmployee | ICenterEmployee[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CenterEmployeeService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      dutyType: DutyType.MAIN,
      joiningDate: currentDate,
      releaseDate: currentDate,
      message: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          joiningDate: currentDate.format(DATE_FORMAT),
          releaseDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a CenterEmployee', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          joiningDate: currentDate.format(DATE_FORMAT),
          releaseDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          joiningDate: currentDate,
          releaseDate: currentDate,
        },
        returnedFromService
      );

      service.create(new CenterEmployee()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CenterEmployee', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          dutyType: 'BBBBBB',
          joiningDate: currentDate.format(DATE_FORMAT),
          releaseDate: currentDate.format(DATE_FORMAT),
          message: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          joiningDate: currentDate,
          releaseDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CenterEmployee', () => {
      const patchObject = Object.assign(
        {
          dutyType: 'BBBBBB',
          releaseDate: currentDate.format(DATE_FORMAT),
          message: 'BBBBBB',
        },
        new CenterEmployee()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          joiningDate: currentDate,
          releaseDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CenterEmployee', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          dutyType: 'BBBBBB',
          joiningDate: currentDate.format(DATE_FORMAT),
          releaseDate: currentDate.format(DATE_FORMAT),
          message: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          joiningDate: currentDate,
          releaseDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a CenterEmployee', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCenterEmployeeToCollectionIfMissing', () => {
      it('should add a CenterEmployee to an empty array', () => {
        const centerEmployee: ICenterEmployee = { id: 'ABC' };
        expectedResult = service.addCenterEmployeeToCollectionIfMissing([], centerEmployee);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(centerEmployee);
      });

      it('should not add a CenterEmployee to an array that contains it', () => {
        const centerEmployee: ICenterEmployee = { id: 'ABC' };
        const centerEmployeeCollection: ICenterEmployee[] = [
          {
            ...centerEmployee,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCenterEmployeeToCollectionIfMissing(centerEmployeeCollection, centerEmployee);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CenterEmployee to an array that doesn't contain it", () => {
        const centerEmployee: ICenterEmployee = { id: 'ABC' };
        const centerEmployeeCollection: ICenterEmployee[] = [{ id: 'CBA' }];
        expectedResult = service.addCenterEmployeeToCollectionIfMissing(centerEmployeeCollection, centerEmployee);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(centerEmployee);
      });

      it('should add only unique CenterEmployee to an array', () => {
        const centerEmployeeArray: ICenterEmployee[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'be399e5a-ac7c-4dfa-9e3f-502a7c18380f' }];
        const centerEmployeeCollection: ICenterEmployee[] = [{ id: 'ABC' }];
        expectedResult = service.addCenterEmployeeToCollectionIfMissing(centerEmployeeCollection, ...centerEmployeeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const centerEmployee: ICenterEmployee = { id: 'ABC' };
        const centerEmployee2: ICenterEmployee = { id: 'CBA' };
        expectedResult = service.addCenterEmployeeToCollectionIfMissing([], centerEmployee, centerEmployee2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(centerEmployee);
        expect(expectedResult).toContain(centerEmployee2);
      });

      it('should accept null and undefined values', () => {
        const centerEmployee: ICenterEmployee = { id: 'ABC' };
        expectedResult = service.addCenterEmployeeToCollectionIfMissing([], null, centerEmployee, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(centerEmployee);
      });

      it('should return initial array if no CenterEmployee is added', () => {
        const centerEmployeeCollection: ICenterEmployee[] = [{ id: 'ABC' }];
        expectedResult = service.addCenterEmployeeToCollectionIfMissing(centerEmployeeCollection, undefined, null);
        expect(expectedResult).toEqual(centerEmployeeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

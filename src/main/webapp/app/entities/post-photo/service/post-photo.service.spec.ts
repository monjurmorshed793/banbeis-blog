import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPostPhoto, PostPhoto } from '../post-photo.model';

import { PostPhotoService } from './post-photo.service';

describe('PostPhoto Service', () => {
  let service: PostPhotoService;
  let httpMock: HttpTestingController;
  let elemDefault: IPostPhoto;
  let expectedResult: IPostPhoto | IPostPhoto[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PostPhotoService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      sequence: 0,
      title: 'AAAAAAA',
      description: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
      uploadedOn: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          uploadedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a PostPhoto', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          uploadedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          uploadedOn: currentDate,
        },
        returnedFromService
      );

      service.create(new PostPhoto()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PostPhoto', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          sequence: 1,
          title: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          uploadedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          uploadedOn: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PostPhoto', () => {
      const patchObject = Object.assign(
        {
          title: 'BBBBBB',
          description: 'BBBBBB',
          uploadedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        new PostPhoto()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          uploadedOn: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PostPhoto', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          sequence: 1,
          title: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          uploadedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          uploadedOn: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a PostPhoto', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPostPhotoToCollectionIfMissing', () => {
      it('should add a PostPhoto to an empty array', () => {
        const postPhoto: IPostPhoto = { id: 'ABC' };
        expectedResult = service.addPostPhotoToCollectionIfMissing([], postPhoto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postPhoto);
      });

      it('should not add a PostPhoto to an array that contains it', () => {
        const postPhoto: IPostPhoto = { id: 'ABC' };
        const postPhotoCollection: IPostPhoto[] = [
          {
            ...postPhoto,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addPostPhotoToCollectionIfMissing(postPhotoCollection, postPhoto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PostPhoto to an array that doesn't contain it", () => {
        const postPhoto: IPostPhoto = { id: 'ABC' };
        const postPhotoCollection: IPostPhoto[] = [{ id: 'CBA' }];
        expectedResult = service.addPostPhotoToCollectionIfMissing(postPhotoCollection, postPhoto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postPhoto);
      });

      it('should add only unique PostPhoto to an array', () => {
        const postPhotoArray: IPostPhoto[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '80e04af7-0282-42f4-a686-ad45f57dcfe3' }];
        const postPhotoCollection: IPostPhoto[] = [{ id: 'ABC' }];
        expectedResult = service.addPostPhotoToCollectionIfMissing(postPhotoCollection, ...postPhotoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const postPhoto: IPostPhoto = { id: 'ABC' };
        const postPhoto2: IPostPhoto = { id: 'CBA' };
        expectedResult = service.addPostPhotoToCollectionIfMissing([], postPhoto, postPhoto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postPhoto);
        expect(expectedResult).toContain(postPhoto2);
      });

      it('should accept null and undefined values', () => {
        const postPhoto: IPostPhoto = { id: 'ABC' };
        expectedResult = service.addPostPhotoToCollectionIfMissing([], null, postPhoto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postPhoto);
      });

      it('should return initial array if no PostPhoto is added', () => {
        const postPhotoCollection: IPostPhoto[] = [{ id: 'ABC' }];
        expectedResult = service.addPostPhotoToCollectionIfMissing(postPhotoCollection, undefined, null);
        expect(expectedResult).toEqual(postPhotoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

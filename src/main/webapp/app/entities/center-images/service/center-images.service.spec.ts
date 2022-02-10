import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICenterImages, CenterImages } from '../center-images.model';

import { CenterImagesService } from './center-images.service';

describe('CenterImages Service', () => {
  let service: CenterImagesService;
  let httpMock: HttpTestingController;
  let elemDefault: ICenterImages;
  let expectedResult: ICenterImages | ICenterImages[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CenterImagesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
      imageUrl: 'AAAAAAA',
      title: 'AAAAAAA',
      description: 'AAAAAAA',
      show: false,
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

    it('should create a CenterImages', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new CenterImages()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CenterImages', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          image: 'BBBBBB',
          imageUrl: 'BBBBBB',
          title: 'BBBBBB',
          description: 'BBBBBB',
          show: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CenterImages', () => {
      const patchObject = Object.assign(
        {
          title: 'BBBBBB',
          description: 'BBBBBB',
        },
        new CenterImages()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CenterImages', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          image: 'BBBBBB',
          imageUrl: 'BBBBBB',
          title: 'BBBBBB',
          description: 'BBBBBB',
          show: true,
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

    it('should delete a CenterImages', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCenterImagesToCollectionIfMissing', () => {
      it('should add a CenterImages to an empty array', () => {
        const centerImages: ICenterImages = { id: 'ABC' };
        expectedResult = service.addCenterImagesToCollectionIfMissing([], centerImages);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(centerImages);
      });

      it('should not add a CenterImages to an array that contains it', () => {
        const centerImages: ICenterImages = { id: 'ABC' };
        const centerImagesCollection: ICenterImages[] = [
          {
            ...centerImages,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCenterImagesToCollectionIfMissing(centerImagesCollection, centerImages);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CenterImages to an array that doesn't contain it", () => {
        const centerImages: ICenterImages = { id: 'ABC' };
        const centerImagesCollection: ICenterImages[] = [{ id: 'CBA' }];
        expectedResult = service.addCenterImagesToCollectionIfMissing(centerImagesCollection, centerImages);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(centerImages);
      });

      it('should add only unique CenterImages to an array', () => {
        const centerImagesArray: ICenterImages[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'c137a360-1324-4bb2-8f46-18a810925423' }];
        const centerImagesCollection: ICenterImages[] = [{ id: 'ABC' }];
        expectedResult = service.addCenterImagesToCollectionIfMissing(centerImagesCollection, ...centerImagesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const centerImages: ICenterImages = { id: 'ABC' };
        const centerImages2: ICenterImages = { id: 'CBA' };
        expectedResult = service.addCenterImagesToCollectionIfMissing([], centerImages, centerImages2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(centerImages);
        expect(expectedResult).toContain(centerImages2);
      });

      it('should accept null and undefined values', () => {
        const centerImages: ICenterImages = { id: 'ABC' };
        expectedResult = service.addCenterImagesToCollectionIfMissing([], null, centerImages, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(centerImages);
      });

      it('should return initial array if no CenterImages is added', () => {
        const centerImagesCollection: ICenterImages[] = [{ id: 'ABC' }];
        expectedResult = service.addCenterImagesToCollectionIfMissing(centerImagesCollection, undefined, null);
        expect(expectedResult).toEqual(centerImagesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT, DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPost, Post } from '../post.model';

import { PostService } from './post.service';

describe('Post Service', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  let elemDefault: IPost;
  let expectedResult: IPost | IPost[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      postDate: currentDate,
      title: 'AAAAAAA',
      body: 'AAAAAAA',
      publish: false,
      publishedOn: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          postDate: currentDate.format(DATE_FORMAT),
          publishedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Post', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          postDate: currentDate.format(DATE_FORMAT),
          publishedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          postDate: currentDate,
          publishedOn: currentDate,
        },
        returnedFromService
      );

      service.create(new Post()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Post', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          postDate: currentDate.format(DATE_FORMAT),
          title: 'BBBBBB',
          body: 'BBBBBB',
          publish: true,
          publishedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          postDate: currentDate,
          publishedOn: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Post', () => {
      const patchObject = Object.assign(
        {
          body: 'BBBBBB',
          publishedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        new Post()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          postDate: currentDate,
          publishedOn: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Post', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          postDate: currentDate.format(DATE_FORMAT),
          title: 'BBBBBB',
          body: 'BBBBBB',
          publish: true,
          publishedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          postDate: currentDate,
          publishedOn: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Post', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPostToCollectionIfMissing', () => {
      it('should add a Post to an empty array', () => {
        const post: IPost = { id: 'ABC' };
        expectedResult = service.addPostToCollectionIfMissing([], post);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(post);
      });

      it('should not add a Post to an array that contains it', () => {
        const post: IPost = { id: 'ABC' };
        const postCollection: IPost[] = [
          {
            ...post,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addPostToCollectionIfMissing(postCollection, post);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Post to an array that doesn't contain it", () => {
        const post: IPost = { id: 'ABC' };
        const postCollection: IPost[] = [{ id: 'CBA' }];
        expectedResult = service.addPostToCollectionIfMissing(postCollection, post);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(post);
      });

      it('should add only unique Post to an array', () => {
        const postArray: IPost[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '50d7f9f6-4b9f-4f04-904d-82562a8fca06' }];
        const postCollection: IPost[] = [{ id: 'ABC' }];
        expectedResult = service.addPostToCollectionIfMissing(postCollection, ...postArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const post: IPost = { id: 'ABC' };
        const post2: IPost = { id: 'CBA' };
        expectedResult = service.addPostToCollectionIfMissing([], post, post2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(post);
        expect(expectedResult).toContain(post2);
      });

      it('should accept null and undefined values', () => {
        const post: IPost = { id: 'ABC' };
        expectedResult = service.addPostToCollectionIfMissing([], null, post, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(post);
      });

      it('should return initial array if no Post is added', () => {
        const postCollection: IPost[] = [{ id: 'ABC' }];
        expectedResult = service.addPostToCollectionIfMissing(postCollection, undefined, null);
        expect(expectedResult).toEqual(postCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

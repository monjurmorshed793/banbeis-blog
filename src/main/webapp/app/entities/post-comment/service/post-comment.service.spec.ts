import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { CommentType } from 'app/entities/enumerations/comment-type.model';
import { IPostComment, PostComment } from '../post-comment.model';

import { PostCommentService } from './post-comment.service';

describe('PostComment Service', () => {
  let service: PostCommentService;
  let httpMock: HttpTestingController;
  let elemDefault: IPostComment;
  let expectedResult: IPostComment | IPostComment[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PostCommentService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      commentedBy: 'AAAAAAA',
      comment: 'AAAAAAA',
      commentType: CommentType.INITIAL_COMMENT,
      commentedOn: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          commentedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a PostComment', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          commentedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          commentedOn: currentDate,
        },
        returnedFromService
      );

      service.create(new PostComment()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PostComment', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          commentedBy: 'BBBBBB',
          comment: 'BBBBBB',
          commentType: 'BBBBBB',
          commentedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          commentedOn: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PostComment', () => {
      const patchObject = Object.assign(
        {
          commentedBy: 'BBBBBB',
          commentedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        new PostComment()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          commentedOn: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PostComment', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          commentedBy: 'BBBBBB',
          comment: 'BBBBBB',
          commentType: 'BBBBBB',
          commentedOn: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          commentedOn: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a PostComment', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPostCommentToCollectionIfMissing', () => {
      it('should add a PostComment to an empty array', () => {
        const postComment: IPostComment = { id: 'ABC' };
        expectedResult = service.addPostCommentToCollectionIfMissing([], postComment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postComment);
      });

      it('should not add a PostComment to an array that contains it', () => {
        const postComment: IPostComment = { id: 'ABC' };
        const postCommentCollection: IPostComment[] = [
          {
            ...postComment,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addPostCommentToCollectionIfMissing(postCommentCollection, postComment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PostComment to an array that doesn't contain it", () => {
        const postComment: IPostComment = { id: 'ABC' };
        const postCommentCollection: IPostComment[] = [{ id: 'CBA' }];
        expectedResult = service.addPostCommentToCollectionIfMissing(postCommentCollection, postComment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postComment);
      });

      it('should add only unique PostComment to an array', () => {
        const postCommentArray: IPostComment[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '792a645d-f011-4a50-a3e4-84c6dfdc2a3e' }];
        const postCommentCollection: IPostComment[] = [{ id: 'ABC' }];
        expectedResult = service.addPostCommentToCollectionIfMissing(postCommentCollection, ...postCommentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const postComment: IPostComment = { id: 'ABC' };
        const postComment2: IPostComment = { id: 'CBA' };
        expectedResult = service.addPostCommentToCollectionIfMissing([], postComment, postComment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postComment);
        expect(expectedResult).toContain(postComment2);
      });

      it('should accept null and undefined values', () => {
        const postComment: IPostComment = { id: 'ABC' };
        expectedResult = service.addPostCommentToCollectionIfMissing([], null, postComment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postComment);
      });

      it('should return initial array if no PostComment is added', () => {
        const postCommentCollection: IPostComment[] = [{ id: 'ABC' }];
        expectedResult = service.addPostCommentToCollectionIfMissing(postCommentCollection, undefined, null);
        expect(expectedResult).toEqual(postCommentCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

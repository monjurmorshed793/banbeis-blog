import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IPostPhoto, PostPhoto } from '../post-photo.model';
import { PostPhotoService } from '../service/post-photo.service';

import { PostPhotoRoutingResolveService } from './post-photo-routing-resolve.service';

describe('PostPhoto routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: PostPhotoRoutingResolveService;
  let service: PostPhotoService;
  let resultPostPhoto: IPostPhoto | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(PostPhotoRoutingResolveService);
    service = TestBed.inject(PostPhotoService);
    resultPostPhoto = undefined;
  });

  describe('resolve', () => {
    it('should return IPostPhoto returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPostPhoto = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultPostPhoto).toEqual({ id: 'ABC' });
    });

    it('should return new IPostPhoto if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPostPhoto = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPostPhoto).toEqual(new PostPhoto());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as PostPhoto })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPostPhoto = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultPostPhoto).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});

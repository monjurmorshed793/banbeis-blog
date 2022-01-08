import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ICenterImages, CenterImages } from '../center-images.model';
import { CenterImagesService } from '../service/center-images.service';

import { CenterImagesRoutingResolveService } from './center-images-routing-resolve.service';

describe('CenterImages routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CenterImagesRoutingResolveService;
  let service: CenterImagesService;
  let resultCenterImages: ICenterImages | undefined;

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
    routingResolveService = TestBed.inject(CenterImagesRoutingResolveService);
    service = TestBed.inject(CenterImagesService);
    resultCenterImages = undefined;
  });

  describe('resolve', () => {
    it('should return ICenterImages returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCenterImages = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCenterImages).toEqual({ id: 'ABC' });
    });

    it('should return new ICenterImages if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCenterImages = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCenterImages).toEqual(new CenterImages());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CenterImages })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCenterImages = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCenterImages).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});

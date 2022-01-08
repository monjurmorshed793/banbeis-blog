import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ICenterEmployee, CenterEmployee } from '../center-employee.model';
import { CenterEmployeeService } from '../service/center-employee.service';

import { CenterEmployeeRoutingResolveService } from './center-employee-routing-resolve.service';

describe('CenterEmployee routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CenterEmployeeRoutingResolveService;
  let service: CenterEmployeeService;
  let resultCenterEmployee: ICenterEmployee | undefined;

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
    routingResolveService = TestBed.inject(CenterEmployeeRoutingResolveService);
    service = TestBed.inject(CenterEmployeeService);
    resultCenterEmployee = undefined;
  });

  describe('resolve', () => {
    it('should return ICenterEmployee returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCenterEmployee = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCenterEmployee).toEqual({ id: 'ABC' });
    });

    it('should return new ICenterEmployee if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCenterEmployee = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCenterEmployee).toEqual(new CenterEmployee());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CenterEmployee })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCenterEmployee = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCenterEmployee).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});

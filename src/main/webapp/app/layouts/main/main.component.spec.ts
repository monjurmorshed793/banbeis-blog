import { Account } from '../../core/auth/account.model';

jest.mock('app/core/auth/account.service');

import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, RouterEvent, NavigationEnd, NavigationStart } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { Subject, of } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';

import { MainComponent } from './main.component';

describe('MainComponent', () => {
  let comp: MainComponent;
  let fixture: ComponentFixture<MainComponent>;
  let titleService: Title;
  let mockAccountService: AccountService;
  const routerEventsSubject = new Subject<RouterEvent>();
  const routerState: any = { snapshot: { root: { data: {} } } };
  class MockRouter {
    events = routerEventsSubject;
    routerState = routerState;
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [MainComponent],
        providers: [
          Title,
          AccountService,
          {
            provide: Router,
            useClass: MockRouter,
          },
        ],
      })
        .overrideTemplate(MainComponent, '')
        .compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MainComponent);
    comp = fixture.componentInstance;
    titleService = TestBed.inject(Title);
    mockAccountService = TestBed.inject(AccountService);
    mockAccountService.identity = jest.fn(() => of(null));
    mockAccountService.getAuthenticationState = jest.fn(() => of());
  });

  describe('page title', () => {
    const defaultPageTitle = 'Banbeis Blog';
    const parentRoutePageTitle = 'parentTitle';
    const childRoutePageTitle = 'childTitle';
    const navigationEnd = new NavigationEnd(1, '', '');
    const navigationStart = new NavigationStart(1, '');

    beforeEach(() => {
      routerState.snapshot.root = { data: {} };
      jest.spyOn(titleService, 'setTitle');
      comp.ngOnInit();
    });

    describe('navigation end', () => {
      it('should set page title to default title if pageTitle is missing on routes', () => {
        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(titleService.setTitle).toHaveBeenCalledWith(defaultPageTitle);
      });

      it('should set page title to root route pageTitle if there is no child routes', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };

        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(titleService.setTitle).toHaveBeenCalledWith(parentRoutePageTitle);
      });

      it('should set page title to child route pageTitle if child routes exist and pageTitle is set for child route', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };
        routerState.snapshot.root.firstChild = { data: { pageTitle: childRoutePageTitle } };

        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(titleService.setTitle).toHaveBeenCalledWith(childRoutePageTitle);
      });

      it('should set page title to parent route pageTitle if child routes exists but pageTitle is not set for child route data', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };
        routerState.snapshot.root.firstChild = { data: {} };

        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(titleService.setTitle).toHaveBeenCalledWith(parentRoutePageTitle);
      });
    });

    describe('navigation start', () => {
      it('should not set page title on navigation start', () => {
        // WHEN
        routerEventsSubject.next(navigationStart);

        // THEN
        expect(titleService.setTitle).not.toHaveBeenCalled();
      });
    });
  });

  describe('show side nav', () => {
    const accountWithMaintainerAuthority: Account = {
      activated: true,
      authorities: ['ROLE_BLOG_MAINTAINER'],
      email: 'random@gmail.com',
      firstName: 'firstName',
      lastName: 'lastName',
      langKey: 'EN',
      login: 'login',
      imageUrl: 'imageUrl',
    };

    const accountWithAdminAuthority: Account = {
      activated: true,
      authorities: ['ROLE_ADMIN'],
      email: 'random@gmail.com',
      firstName: 'firstName',
      lastName: 'lastName',
      langKey: 'EN',
      login: 'login',
      imageUrl: 'imageUrl',
    };

    const accountWithoutMaintainerAuthority: Account = {
      activated: true,
      authorities: ['ROLE_BRANCH_MAINTAINER'],
      email: 'random@gmail.com',
      firstName: 'firstName',
      lastName: 'lastName',
      langKey: 'EN',
      login: 'login',
      imageUrl: 'imageUrl',
    };

    describe('after ngOnInt() is called', () => {
      it('should set showSidenav to true if ROLE_BLOG_MAINTAINER role is found in the user account', () => {
        // GIVEN
        jest.spyOn(mockAccountService, 'getAuthenticationState').mockReturnValue(of(accountWithMaintainerAuthority));

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.showSideNav).toEqual(true);
      });

      it('should set showSidenav to true if ROLE_ADMIN role is found in the user account', () => {
        // GIVEN
        jest.spyOn(mockAccountService, 'getAuthenticationState').mockReturnValue(of(accountWithAdminAuthority));

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.showSideNav).toEqual(true);
      });

      it('should set showSidenav to false if ROLE_BLOG_MAINTAINER role is found in the user account', () => {
        // GIVEN
        jest.spyOn(mockAccountService, 'getAuthenticationState').mockReturnValue(of(accountWithoutMaintainerAuthority));

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.showSideNav).toEqual(false);
      });
    });
  });
});

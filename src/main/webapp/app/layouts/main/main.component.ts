import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, ActivatedRouteSnapshot, NavigationEnd } from '@angular/router';

import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.component.html',
})
export class MainComponent implements OnInit {
  showSideNav = false;
  openSidenav = true;
  constructor(private accountService: AccountService, private titleService: Title, private router: Router) {}

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.accountService.getAuthenticationState().subscribe(res => {
      // eslint-disable-next-line no-console
      console.group('Logged user authentication state');
      // eslint-disable-next-line no-console
      console.log(res);
      if (res?.authorities.includes('ROLE_BLOG_MAINTAINER') || res?.authorities.includes('ROLE_ADMIN')) {
        // eslint-disable-next-line no-console
        console.log('show sidenav --> true');
        this.showSideNav = true;
      } else {
        // eslint-disable-next-line no-console
        console.log('show sidenav --> false');
        this.showSideNav = false;
      }
      // eslint-disable-next-line no-console
      console.groupEnd();
    });

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateTitle();
      }
    });
  }

  private getPageTitle(routeSnapshot: ActivatedRouteSnapshot): string {
    const title: string = routeSnapshot.data['pageTitle'] ?? '';
    if (routeSnapshot.firstChild) {
      return this.getPageTitle(routeSnapshot.firstChild) || title;
    }
    return title;
  }

  private updateTitle(): void {
    let pageTitle = this.getPageTitle(this.router.routerState.snapshot.root);
    if (!pageTitle) {
      pageTitle = 'Banbeis Blog';
    }
    this.titleService.setTitle(pageTitle);
  }
}

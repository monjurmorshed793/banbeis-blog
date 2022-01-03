import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INavigation } from '../navigation.model';

@Component({
  selector: 'jhi-navigation-detail',
  templateUrl: './navigation-detail.component.html',
})
export class NavigationDetailComponent implements OnInit {
  navigation: INavigation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ navigation }) => {
      this.navigation = navigation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

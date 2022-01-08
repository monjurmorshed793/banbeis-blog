import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUpazila } from '../upazila.model';

@Component({
  selector: 'jhi-upazila-detail',
  templateUrl: './upazila-detail.component.html',
})
export class UpazilaDetailComponent implements OnInit {
  upazila: IUpazila | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ upazila }) => {
      this.upazila = upazila;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

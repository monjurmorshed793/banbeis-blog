import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDesignation } from '../designation.model';

@Component({
  selector: 'jhi-designation-detail',
  templateUrl: './designation-detail.component.html',
})
export class DesignationDetailComponent implements OnInit {
  designation: IDesignation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ designation }) => {
      this.designation = designation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

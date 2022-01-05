import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICenterEmployee } from '../center-employee.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-center-employee-detail',
  templateUrl: './center-employee-detail.component.html',
})
export class CenterEmployeeDetailComponent implements OnInit {
  centerEmployee: ICenterEmployee | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centerEmployee }) => {
      this.centerEmployee = centerEmployee;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}

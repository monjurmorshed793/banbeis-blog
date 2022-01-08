import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICenter } from '../center.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-center-detail',
  templateUrl: './center-detail.component.html',
})
export class CenterDetailComponent implements OnInit {
  center: ICenter | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ center }) => {
      this.center = center;
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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICenterImages } from '../center-images.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-center-images-detail',
  templateUrl: './center-images-detail.component.html',
})
export class CenterImagesDetailComponent implements OnInit {
  centerImages: ICenterImages | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centerImages }) => {
      this.centerImages = centerImages;
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

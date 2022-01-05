import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPostPhoto } from '../post-photo.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-post-photo-detail',
  templateUrl: './post-photo-detail.component.html',
})
export class PostPhotoDetailComponent implements OnInit {
  postPhoto: IPostPhoto | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ postPhoto }) => {
      this.postPhoto = postPhoto;
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

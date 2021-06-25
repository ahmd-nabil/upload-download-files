import { HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FileService } from './file.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  filenames: Array<string> = [];
  loadStatus = {status: '', requestType: '', percent: 0};

  constructor(private fileService: FileService) {
  }

  ngOnInit() {
    this.fileService.loadFiles().subscribe(result => this.filenames = result);
  }

  onUploadFiles(files: Array<File>): void {
    const formData = new FormData();
    for(const file of files) {
      formData.append('files', file, file.name);
    }
    this.fileService.upload(formData).subscribe(
      event => {
        console.log(event);
        this.reportProgress(event);
      },
      error => {
        console.log(error);
      }
    );
  }

  onDownloadFile(filename: string): void {
    this.fileService.download(filename).subscribe(
      event => {
        console.log(event);
      },
      error => {
        console.log(error);
      } 
    );
  }

  private reportProgress(httpEvent: HttpEvent<string[] | Blob>): void {
    switch(httpEvent.type) {
      case HttpEventType.UploadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total!, 'Uploading...');
        break;
      case HttpEventType.ResponseHeader:
        console.log("Header returned: ", httpEvent);
        break;
      case HttpEventType.Response:
        if(httpEvent.body instanceof Array) {
          // it was upload
          for(const filename of httpEvent.body) {
            this.filenames.unshift(filename); // insert to the beginning of array
          }
          this.loadStatus.status = 'done';
        }
        break;
      default:
        console.log(httpEvent);
    }
  }

  private updateStatus(loaded: number, total: number, type: string): void {
    this.loadStatus.status = 'progress';
    this.loadStatus.requestType = type;
    this.loadStatus.percent = Math.round(100 * loaded / total);
  }
}

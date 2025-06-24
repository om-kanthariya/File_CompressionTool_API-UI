import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-file-compression-template',
  standalone:false,
  templateUrl: './file-compression-template.component.html',
  styleUrls: ['./file-compression-template.component.css']
})
export class FileCompressionTemplateComponent {
 selectedFile: File | null = null;
  compressedFileUrl: string | null = null;

  constructor(private http: HttpClient) {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  compressFile() {
    if (!this.selectedFile) return;

    const formData = new FormData();
    formData.append("file", this.selectedFile);

    this.http.post('http://localhost:8080/api/files/compress', formData, {
      responseType: 'blob'
    }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = this.selectedFile!.name + '.gz';
      a.click();
    });
  }
}

import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-huffman-compressor',
  templateUrl: './huffman-compressor.component.html',
  styleUrls: ['./huffman-compressor.component.css']
})
export class HuffmanCompressorComponent {
selectedFile: File | null = null;
  compressionResult: any = null;

  constructor(private http: HttpClient) {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  uploadFile() {
    if (!this.selectedFile) return;

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post<any>('http://localhost:8080/api/files/compress', formData)
      .subscribe({
        next: data => this.compressionResult = data,
        error: err => alert('Compression failed: ' + err.message)
      });
  }

  downloadCompressedFile() {
    this.http.get('http://localhost:8080/api/files/download-compressed', {
      responseType: 'blob'
    }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'compressed.huff';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}

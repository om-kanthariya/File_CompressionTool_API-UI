import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // For ngModel (two-way binding)
import { HttpClientModule } from '@angular/common/http'; // For API calls
import { AppComponent } from './app.component';
import { FileCompressionTemplateComponent } from './file-compression-template/file-compression-template.component';
import { HuffmanCompressorComponent } from "./huffman-compressor/huffman-compressor.component";
import { CommonModule } from '@angular/common';


@NgModule({
  declarations: [
    AppComponent,
    FileCompressionTemplateComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    HuffmanCompressorComponent
],
  providers: [],
  bootstrap: [AppComponent] // This is the root component Angular will load first
})
export class AppModule { }

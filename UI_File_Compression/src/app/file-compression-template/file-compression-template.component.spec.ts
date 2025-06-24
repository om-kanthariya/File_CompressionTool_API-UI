import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileCompressionTemplateComponent } from './file-compression-template.component';

describe('FileCompressionTemplateComponent', () => {
  let component: FileCompressionTemplateComponent;
  let fixture: ComponentFixture<FileCompressionTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FileCompressionTemplateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FileCompressionTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HuffmanCompressorComponent } from './huffman-compressor.component';

describe('HuffmanCompressorComponent', () => {
  let component: HuffmanCompressorComponent;
  let fixture: ComponentFixture<HuffmanCompressorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HuffmanCompressorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HuffmanCompressorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResponseFormComponent } from './response-form.component';

describe('ResponseFormComponent', () => {
  let component: ResponseFormComponent;
  let fixture: ComponentFixture<ResponseFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponseFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ResponseFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

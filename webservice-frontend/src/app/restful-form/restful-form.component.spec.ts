import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestfulFormComponent } from './restful-form.component';

describe('RestfulFormComponent', () => {
  let component: RestfulFormComponent;
  let fixture: ComponentFixture<RestfulFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestfulFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RestfulFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

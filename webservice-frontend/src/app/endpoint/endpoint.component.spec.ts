import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EndpointComponent } from './endpoint.component';

describe('EndpointComponent', () => {
  let component: EndpointComponent;
  let fixture: ComponentFixture<EndpointComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EndpointComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EndpointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

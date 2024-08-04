import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterServiceRequestComponent } from './register-service-request.component';

describe('RegisterServiceRequestComponent', () => {
  let component: RegisterServiceRequestComponent;
  let fixture: ComponentFixture<RegisterServiceRequestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterServiceRequestComponent]
    });
    fixture = TestBed.createComponent(RegisterServiceRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterIncidentComponent } from './register-incident.component';

describe('RegisterIncidentComponent', () => {
  let component: RegisterIncidentComponent;
  let fixture: ComponentFixture<RegisterIncidentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RegisterIncidentComponent]
    });
    fixture = TestBed.createComponent(RegisterIncidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

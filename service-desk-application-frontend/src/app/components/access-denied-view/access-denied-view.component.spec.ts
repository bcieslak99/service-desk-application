import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccessDeniedViewComponent } from './access-denied-view.component';

describe('AccessDeniedViewComponent', () => {
  let component: AccessDeniedViewComponent;
  let fixture: ComponentFixture<AccessDeniedViewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccessDeniedViewComponent]
    });
    fixture = TestBed.createComponent(AccessDeniedViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

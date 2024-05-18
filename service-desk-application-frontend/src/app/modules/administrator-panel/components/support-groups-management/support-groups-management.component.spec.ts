import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportGroupsManagementComponent } from './support-groups-management.component';

describe('SupportGroupsManagementComponent', () => {
  let component: SupportGroupsManagementComponent;
  let fixture: ComponentFixture<SupportGroupsManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SupportGroupsManagementComponent]
    });
    fixture = TestBed.createComponent(SupportGroupsManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

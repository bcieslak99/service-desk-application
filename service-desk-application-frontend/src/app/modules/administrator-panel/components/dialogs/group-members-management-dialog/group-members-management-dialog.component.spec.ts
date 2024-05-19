import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupMembersManagementDialogComponent } from './group-members-management-dialog.component';

describe('GroupMembersManagementDialogComponent', () => {
  let component: GroupMembersManagementDialogComponent;
  let fixture: ComponentFixture<GroupMembersManagementDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GroupMembersManagementDialogComponent]
    });
    fixture = TestBed.createComponent(GroupMembersManagementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

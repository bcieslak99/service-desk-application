import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupManagerDialogComponent } from './group-manager-dialog.component';

describe('GroupManagerDialogComponent', () => {
  let component: GroupManagerDialogComponent;
  let fixture: ComponentFixture<GroupManagerDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GroupManagerDialogComponent]
    });
    fixture = TestBed.createComponent(GroupManagerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

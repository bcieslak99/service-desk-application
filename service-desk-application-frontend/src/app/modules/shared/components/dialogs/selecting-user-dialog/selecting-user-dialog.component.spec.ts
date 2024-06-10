import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectingUserDialogComponent } from './selecting-user-dialog.component';

describe('SelectingUserDialogComponent', () => {
  let component: SelectingUserDialogComponent;
  let fixture: ComponentFixture<SelectingUserDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SelectingUserDialogComponent]
    });
    fixture = TestBed.createComponent(SelectingUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

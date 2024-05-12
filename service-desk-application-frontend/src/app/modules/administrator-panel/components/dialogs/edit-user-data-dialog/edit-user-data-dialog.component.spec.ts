import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditUserDataDialogComponent } from './edit-user-data-dialog.component';

describe('EditUserDataDialogComponent', () => {
  let component: EditUserDataDialogComponent;
  let fixture: ComponentFixture<EditUserDataDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditUserDataDialogComponent]
    });
    fixture = TestBed.createComponent(EditUserDataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

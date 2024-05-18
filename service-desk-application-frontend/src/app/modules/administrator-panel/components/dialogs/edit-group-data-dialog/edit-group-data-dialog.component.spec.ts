import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditGroupDataDialogComponent } from './edit-group-data-dialog.component';

describe('EditGroupDataDialogComponent', () => {
  let component: EditGroupDataDialogComponent;
  let fixture: ComponentFixture<EditGroupDataDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditGroupDataDialogComponent]
    });
    fixture = TestBed.createComponent(EditGroupDataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

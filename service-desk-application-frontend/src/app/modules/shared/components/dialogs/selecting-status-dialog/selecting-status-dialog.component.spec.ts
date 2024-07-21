import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectingStatusDialogComponent } from './selecting-status-dialog.component';

describe('SelectingStatusDialogComponent', () => {
  let component: SelectingStatusDialogComponent;
  let fixture: ComponentFixture<SelectingStatusDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SelectingStatusDialogComponent]
    });
    fixture = TestBed.createComponent(SelectingStatusDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

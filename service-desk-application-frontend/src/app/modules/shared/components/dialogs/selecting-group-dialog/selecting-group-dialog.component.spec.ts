import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectingGroupDialogComponent } from './selecting-group-dialog.component';

describe('SelectingGroupDialogComponent', () => {
  let component: SelectingGroupDialogComponent;
  let fixture: ComponentFixture<SelectingGroupDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SelectingGroupDialogComponent]
    });
    fixture = TestBed.createComponent(SelectingGroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

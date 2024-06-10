import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectingCategoryDialogComponent } from './selecting-category-dialog.component';

describe('SelectingCategoryDialogComponent', () => {
  let component: SelectingCategoryDialogComponent;
  let fixture: ComponentFixture<SelectingCategoryDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SelectingCategoryDialogComponent]
    });
    fixture = TestBed.createComponent(SelectingCategoryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

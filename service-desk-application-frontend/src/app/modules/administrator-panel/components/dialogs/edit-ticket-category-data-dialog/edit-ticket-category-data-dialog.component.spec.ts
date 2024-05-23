import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTicketCategoryDataDialogComponent } from './edit-ticket-category-data-dialog.component';

describe('EditTicketCategoryDataDialogComponent', () => {
  let component: EditTicketCategoryDataDialogComponent;
  let fixture: ComponentFixture<EditTicketCategoryDataDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditTicketCategoryDataDialogComponent]
    });
    fixture = TestBed.createComponent(EditTicketCategoryDataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

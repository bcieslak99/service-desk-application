import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketCategoriesManagementComponent } from './ticket-categories-management.component';

describe('TicketCategoriesManagementComponent', () => {
  let component: TicketCategoriesManagementComponent;
  let fixture: ComponentFixture<TicketCategoriesManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TicketCategoriesManagementComponent]
    });
    fixture = TestBed.createComponent(TicketCategoriesManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

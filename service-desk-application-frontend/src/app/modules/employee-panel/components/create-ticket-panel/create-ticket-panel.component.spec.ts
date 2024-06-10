import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateTicketPanelComponent } from './create-ticket-panel.component';

describe('CreateTicketPanelComponent', () => {
  let component: CreateTicketPanelComponent;
  let fixture: ComponentFixture<CreateTicketPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateTicketPanelComponent]
    });
    fixture = TestBed.createComponent(CreateTicketPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

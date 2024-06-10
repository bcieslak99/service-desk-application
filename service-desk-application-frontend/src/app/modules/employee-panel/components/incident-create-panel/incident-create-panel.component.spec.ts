import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IncidentCreatePanelComponent } from './incident-create-panel.component';

describe('IncidentCreatePanelComponent', () => {
  let component: IncidentCreatePanelComponent;
  let fixture: ComponentFixture<IncidentCreatePanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [IncidentCreatePanelComponent]
    });
    fixture = TestBed.createComponent(IncidentCreatePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

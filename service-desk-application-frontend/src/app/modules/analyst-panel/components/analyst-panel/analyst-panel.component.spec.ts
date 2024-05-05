import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalystPanelComponent } from './analyst-panel.component';

describe('AnalystPanelComponent', () => {
  let component: AnalystPanelComponent;
  let fixture: ComponentFixture<AnalystPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AnalystPanelComponent]
    });
    fixture = TestBed.createComponent(AnalystPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

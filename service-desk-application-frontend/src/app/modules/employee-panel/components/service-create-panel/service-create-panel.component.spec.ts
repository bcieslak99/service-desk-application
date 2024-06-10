import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceCreatePanelComponent } from './service-create-panel.component';

describe('ServiceCreatePanelComponent', () => {
  let component: ServiceCreatePanelComponent;
  let fixture: ComponentFixture<ServiceCreatePanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceCreatePanelComponent]
    });
    fixture = TestBed.createComponent(ServiceCreatePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

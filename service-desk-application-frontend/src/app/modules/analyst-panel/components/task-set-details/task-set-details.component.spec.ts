import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskSetDetailsComponent } from './task-set-details.component';

describe('TaskSetDetailsComponent', () => {
  let component: TaskSetDetailsComponent;
  let fixture: ComponentFixture<TaskSetDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TaskSetDetailsComponent]
    });
    fixture = TestBed.createComponent(TaskSetDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

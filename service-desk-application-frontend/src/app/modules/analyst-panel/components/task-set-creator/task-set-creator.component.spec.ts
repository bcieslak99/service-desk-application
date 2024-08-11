import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskSetCreatorComponent } from './task-set-creator.component';

describe('TaskSetCreatorComponent', () => {
  let component: TaskSetCreatorComponent;
  let fixture: ComponentFixture<TaskSetCreatorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TaskSetCreatorComponent]
    });
    fixture = TestBed.createComponent(TaskSetCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogNoteEditorComponent } from './dialog-note-editor.component';

describe('DialogNoteEditorComponent', () => {
  let component: DialogNoteEditorComponent;
  let fixture: ComponentFixture<DialogNoteEditorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DialogNoteEditorComponent]
    });
    fixture = TestBed.createComponent(DialogNoteEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

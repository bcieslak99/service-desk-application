import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NoteForm} from "../../../../models/note-interfaces";

@Component({
  selector: 'app-dialog-note-editor',
  templateUrl: './dialog-note-editor.component.html',
  styleUrls: ['./dialog-note-editor.component.css']
})
export class DialogNoteEditorComponent
{
  title: string = "";
  description: string = "";

  constructor(public dialog: MatDialogRef<DialogNoteEditorComponent>, @Inject(MAT_DIALOG_DATA) public data: NoteForm)
  {
    this.title = data.title;
    this.description = data.description;
  }

  closeDialog(result: NoteForm | null): void
  {
    this.dialog.close(result);
  }

  cancel(): void
  {
    this.closeDialog(null);
  }

  confirm(): void
  {
    const result: NoteForm = {
      title: this.title,
      description: this.description
    };

    this.closeDialog(result);
  }
}

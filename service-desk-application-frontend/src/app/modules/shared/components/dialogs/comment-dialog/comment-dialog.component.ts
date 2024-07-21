import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DataToCommentDialogInitialisation} from "../../../../../models/ticket.interfaces";

@Component({
  selector: 'app-comment-dialog',
  templateUrl: './comment-dialog.component.html',
  styleUrls: ['./comment-dialog.component.css']
})
export class CommentDialogComponent
{
  title: string = "Dodaj komentarz";
  comment: string = "";

  constructor(public dialog: MatDialogRef<CommentDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: DataToCommentDialogInitialisation)
  {
    this.title = data.title;
    this.comment = data.comment;
  }

  commentIsCorrect(): boolean
  {
    return this.comment.trim().length > 0;
  }

  closeDialog(data: string | null): void
  {
    this.dialog.close(data);
  }

  submit(): void
  {
    this.closeDialog(this.comment);
  }

  cancel(): void
  {
    this.closeDialog(null);
  }
}

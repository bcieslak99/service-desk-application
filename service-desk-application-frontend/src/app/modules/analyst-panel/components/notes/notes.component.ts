import {AfterViewInit, Component} from '@angular/core';
import {NoteService} from "../services/note.service";
import {Note, NoteForm} from "../../../../models/note-interfaces";
import {NotifierService} from "angular-notifier";
import {MatDialog} from "@angular/material/dialog";
import {DialogNoteEditorComponent} from "../dialog-note-editor/dialog-note-editor.component";

@Component({
  selector: 'app-notes',
  templateUrl: './notes.component.html',
  styleUrls: ['./notes.component.css']
})
export class NotesComponent implements AfterViewInit
{
  notes: Note[] = [];

  constructor(private http: NoteService, private notifier: NotifierService, private dialog: MatDialog) {}

  convertText(text: string): string
  {
    return text.replace(/\n/g, '<br>');
  }

  loadNotes(): void
  {
    this.http.getNotes().subscribe({
      next: notes =>  {
        this.notes = notes;
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać notatek!");
      }
    });
  }

  openNoteEditor(data: Note | null)
  {
    if(data === null) return this.dialog.open(DialogNoteEditorComponent, {data: {title: "", description: ""}});
    else return this.dialog.open(DialogNoteEditorComponent, {data: {title: data.title, description: data.description}});
  }

  createNote(): void
  {
    this.openNoteEditor(null).afterClosed().subscribe(result => {
      if(result !== null && result !== undefined) this.http.createNote(result).subscribe({
        next: note => {
          this.notifier.notify("success", "Notatka została utworzona.");
          this.loadNotes();
        },
        error: err => {
          this.notifier.notify("error", "Napotkano na błąd podczas tworzenia notatki!");
        }
      });
    });
  }

  deleteNote(note: Note): void
  {
    this.http.deleteNote(note.id).subscribe({
      next: result => {
        this.loadNotes();
        this.notifier.notify("success", "Notatka zostałą usunięta");
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się usunąć notatki!");
      }
    });
  }

  editNote(note: Note): void
  {
    this.openNoteEditor(note).afterClosed().subscribe(data => {
      this.http.editNote(note.id, data).subscribe({
        next: result => {
          this.loadNotes();
          this.notifier.notify("success", "Notatka została edytowana");
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się edytować notatki!");
        }
      })
    });
  }

  ngAfterViewInit(): void
  {
    this.loadNotes();
  }
}

import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Note, NoteForm} from "../../../../models/note-interfaces";
import {ApplicationSettings} from "../../../../application-settings";
import {ServerResponsesMessage} from "../../../../models/server-responses.interfaces";

@Injectable({
  providedIn: 'root'
})
export class NoteService
{
  constructor(private http: HttpClient) {}

  getNotes()
  {
    return this.http.get<Note[]>(ApplicationSettings.apiUrl + "/api/v1/note/list");
  }

  createNote(note: NoteForm)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/note/create", note);
  }

  deleteNote(noteId: string)
  {
    return this.http.delete<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/note/delete/" + noteId);
  }

  editNote(noteId: string, note: NoteForm)
  {
    return this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/note/edit/" + noteId, note);
  }
}

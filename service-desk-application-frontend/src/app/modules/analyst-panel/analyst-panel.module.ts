import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AnalystPanelRoutingModule} from "./analyst-panel-routing.module";
import { AnalystPanelComponent } from './components/analyst-panel/analyst-panel.component';
import {MatTreeModule} from "@angular/material/tree";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { TicketListComponent } from './components/ticket-list/ticket-list.component';
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatTableModule} from "@angular/material/table";
import { RegisterTicketComponent } from './components/register/register-ticket/register-ticket.component';
import { RegisterIncidentComponent } from './components/register/register-incident/register-incident.component';
import { RegisterServiceRequestComponent } from './components/register/register-service-request/register-service-request.component';
import {SharedModule} from "../shared/shared.module";
import { NotesComponent } from './components/notes/notes.component';
import { DialogNoteEditorComponent } from './components/dialog-note-editor/dialog-note-editor.component';
import {MatDialogModule} from "@angular/material/dialog";

@NgModule({
  declarations: [
    AnalystPanelComponent,
    TicketListComponent,
    RegisterTicketComponent,
    RegisterIncidentComponent,
    RegisterServiceRequestComponent,
    NotesComponent,
    DialogNoteEditorComponent
  ],
  imports: [
    CommonModule,
    AnalystPanelRoutingModule,
    MatTreeModule,
    MatIconModule,
    MatButtonModule,
    MatInputModule,
    MatCardModule,
    FormsModule,
    MatPaginatorModule,
    MatSortModule,
    MatTableModule,
    ReactiveFormsModule,
    SharedModule,
    MatDialogModule
  ]
})
export class AnalystPanelModule {}

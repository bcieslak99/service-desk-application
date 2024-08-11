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
import { TasksListComponent } from './components/tasks-list/tasks-list.component';
import { TaskSetCreatorComponent } from './components/task-set-creator/task-set-creator.component';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import { TaskSetDetailsComponent } from './components/task-set-details/task-set-details.component';

@NgModule({
  declarations: [
    AnalystPanelComponent,
    TicketListComponent,
    RegisterTicketComponent,
    RegisterIncidentComponent,
    RegisterServiceRequestComponent,
    NotesComponent,
    DialogNoteEditorComponent,
    TasksListComponent,
    TaskSetCreatorComponent,
    TaskSetDetailsComponent
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
    MatDialogModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule
  ]
})
export class AnalystPanelModule {}

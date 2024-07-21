import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectingCategoryDialogComponent } from './components/dialogs/selecting-category-dialog/selecting-category-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatTableModule} from "@angular/material/table";
import {MatSortModule} from "@angular/material/sort";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import { SelectingUserDialogComponent } from './components/dialogs/selecting-user-dialog/selecting-user-dialog.component';
import { TicketDetailsComponent } from './components/ticket-details/ticket-details.component';
import {SharedRouting} from "./shared-routing";
import {MatCardModule} from "@angular/material/card";
import {MatMenuModule} from "@angular/material/menu";
import { SelectingStatusDialogComponent } from './components/dialogs/selecting-status-dialog/selecting-status-dialog.component';
import { CommentDialogComponent } from './components/dialogs/comment-dialog/comment-dialog.component';
import { SelectingGroupDialogComponent } from './components/dialogs/selecting-group-dialog/selecting-group-dialog.component';

@NgModule({
  declarations: [
    SelectingCategoryDialogComponent,
    SelectingUserDialogComponent,
    TicketDetailsComponent,
    SelectingStatusDialogComponent,
    CommentDialogComponent,
    SelectingGroupDialogComponent
  ],
    imports: [
        CommonModule,
        MatDialogModule,
        MatInputModule,
        FormsModule,
        MatTableModule,
        MatSortModule,
        MatPaginatorModule,
        MatButtonModule,
        MatIconModule,
        SharedRouting,
        MatCardModule,
        MatMenuModule
    ]
})
export class SharedModule { }

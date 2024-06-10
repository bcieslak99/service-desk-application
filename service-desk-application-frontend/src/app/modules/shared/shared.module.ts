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

@NgModule({
  declarations: [
    SelectingCategoryDialogComponent,
    SelectingUserDialogComponent
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
    MatIconModule
  ]
})
export class SharedModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AdministratorPanelRoutingModule} from "./administrator-panel-routing.module";
import { AdministratorPanelComponent } from './components/administrator-panel/administrator-panel.component';
import {MatTabsModule} from "@angular/material/tabs";
import { UsersManagementComponent } from './components/users-management/users-management.component';
import {MatTableModule} from "@angular/material/table";
import {MatSortModule} from "@angular/material/sort";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";
import {MatMenuModule} from "@angular/material/menu";
import { NewUserDialogComponent } from './components/dialogs/new-user-dialog/new-user-dialog.component';

@NgModule({
  declarations: [
    AdministratorPanelComponent,
    UsersManagementComponent,
    NewUserDialogComponent
  ],
  imports: [
    CommonModule,
    AdministratorPanelRoutingModule,
    MatTabsModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
    MatMenuModule
  ]
})
export class AdministratorPanelModule { }

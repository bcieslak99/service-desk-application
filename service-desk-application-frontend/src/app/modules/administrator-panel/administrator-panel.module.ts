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
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatMenuModule} from "@angular/material/menu";
import { NewUserDialogComponent } from './components/dialogs/new-user-dialog/new-user-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { ChangePasswordDialogComponent } from './components/dialogs/change-password-dialog/change-password-dialog.component';
import { EditUserDataDialogComponent } from './components/dialogs/edit-user-data-dialog/edit-user-data-dialog.component';
import { SupportGroupsManagementComponent } from './components/support-groups-management/support-groups-management.component';
import { NewGroupDialogComponent } from './components/dialogs/new-group-dialog/new-group-dialog.component';
import {MatSelectModule} from "@angular/material/select";
import { EditGroupDataDialogComponent } from './components/dialogs/edit-group-data-dialog/edit-group-data-dialog.component';
import { GroupMembersManagementDialogComponent } from './components/dialogs/group-members-management-dialog/group-members-management-dialog.component';
import { GroupManagerDialogComponent } from './components/dialogs/group-manager-dialog/group-manager-dialog.component';

@NgModule({
  declarations: [
    AdministratorPanelComponent,
    UsersManagementComponent,
    NewUserDialogComponent,
    ChangePasswordDialogComponent,
    EditUserDataDialogComponent,
    SupportGroupsManagementComponent,
    NewGroupDialogComponent,
    EditGroupDataDialogComponent,
    GroupMembersManagementDialogComponent,
    GroupManagerDialogComponent,
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
    MatMenuModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatCheckboxModule,
    MatSelectModule
  ]
})
export class AdministratorPanelModule { }

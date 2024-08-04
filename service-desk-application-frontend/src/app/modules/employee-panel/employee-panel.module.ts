import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeePanelComponent } from './components/employee-panel/employee-panel.component';
import {EmployeePanelRoutingModule} from "./employee-panel-routing.module";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import { CreateTicketPanelComponent } from './components/create-ticket-panel/create-ticket-panel.component';
import { IncidentCreatePanelComponent } from './components/incident-create-panel/incident-create-panel.component';
import { ServiceCreatePanelComponent } from './components/service-create-panel/service-create-panel.component';
import {SharedModule} from "../shared/shared.module";
import {MatInputModule} from "@angular/material/input";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {ReactiveFormsModule} from "@angular/forms";
import {MatDialogModule} from "@angular/material/dialog";
import { TicketListComponent } from './components/ticket-list/ticket-list.component';
import {MatMenuModule} from "@angular/material/menu";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatTableModule} from "@angular/material/table";

@NgModule({
    declarations: [
        EmployeePanelComponent,
        CreateTicketPanelComponent,
        IncidentCreatePanelComponent,
        ServiceCreatePanelComponent,
        TicketListComponent
    ],
    exports: [
        TicketListComponent
    ],
    imports: [
        CommonModule,
        EmployeePanelRoutingModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        SharedModule,
        MatInputModule,
        MatAutocompleteModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatMenuModule,
        MatPaginatorModule,
        MatSortModule,
        MatTableModule
    ]
})
export class EmployeePanelModule { }

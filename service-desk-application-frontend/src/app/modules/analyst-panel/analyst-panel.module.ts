import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AnalystPanelRoutingModule} from "./analyst-panel-routing.module";
import { AnalystPanelComponent } from './components/analyst-panel/analyst-panel.component';
import {MatTreeModule} from "@angular/material/tree";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {FormsModule} from "@angular/forms";
import { TicketListComponent } from './components/ticket-list/ticket-list.component';
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatTableModule} from "@angular/material/table";



@NgModule({
  declarations: [
    AnalystPanelComponent,
    TicketListComponent
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
        MatTableModule
    ]
})
export class AnalystPanelModule {}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeePanelComponent } from './components/employee-panel/employee-panel.component';
import {EmployeePanelRoutingModule} from "./employee-panel-routing.module";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";



@NgModule({
  declarations: [
    EmployeePanelComponent
  ],
  imports: [
    CommonModule,
    EmployeePanelRoutingModule,
    MatCardModule,
    MatButtonModule
  ]
})
export class EmployeePanelModule { }

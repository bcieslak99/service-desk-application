import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeePanelComponent } from './components/employee-panel/employee-panel.component';
import {EmployeePanelRoutingModule} from "./employee-panel-routing.module";



@NgModule({
  declarations: [
    EmployeePanelComponent
  ],
  imports: [
    CommonModule,
    EmployeePanelRoutingModule
  ]
})
export class EmployeePanelModule { }

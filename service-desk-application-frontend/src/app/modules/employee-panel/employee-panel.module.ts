import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeePanelComponent } from './components/employee-panel/employee-panel.component';
import {EmployeePanelRouting} from "./employee-panel-routing";



@NgModule({
  declarations: [
    EmployeePanelComponent
  ],
  imports: [
    CommonModule,
    EmployeePanelRouting
  ]
})
export class EmployeePanelModule { }

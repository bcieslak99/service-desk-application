import { Component } from '@angular/core';
import {EmployeePanelService} from "../../services/employee-panel.service";

@Component({
  selector: 'app-employee-panel',
  templateUrl: './employee-panel.component.html',
  styleUrls: ['./employee-panel.component.css']
})
export class EmployeePanelComponent
{
  constructor(private employeePanelService: EmployeePanelService) {}
}

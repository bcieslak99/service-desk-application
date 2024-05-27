import {AfterViewInit, Component, OnInit} from '@angular/core';
import {EmployeePanelService} from "../../services/employee-panel.service";
import {EmployeeTicketTypeStatistics} from "../../../../models/ticket.interfaces";

@Component({
  selector: 'app-employee-panel',
  templateUrl: './employee-panel.component.html',
  styleUrls: ['./employee-panel.component.css']
})
export class EmployeePanelComponent implements OnInit
{
  constructor(private employeePanelService: EmployeePanelService) {}

  getEmployeeTicketStatistics(): EmployeeTicketTypeStatistics
  {
    return this.employeePanelService.employeeTicketStatistics;
  }

  ngOnInit(): void
  {
    this.employeePanelService.loadEmployeeTicketStatistics();
  }
}

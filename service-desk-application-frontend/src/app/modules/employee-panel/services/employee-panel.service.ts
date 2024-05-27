import { Injectable } from '@angular/core';
import {EmployeeHttpService} from "./employee-http.service";
import {EmployeeTicketTypeStatistics} from "../../../models/ticket.interfaces";
import {NotifierService} from "angular-notifier";

@Injectable({
  providedIn: 'root'
})
export class EmployeePanelService
{
  employeeTicketStatistics: EmployeeTicketTypeStatistics = {
    incidents: {
      inProgress: 0,
      onHold: 0,
      pending: 0,
      resolved: 0
    },
    serviceRequests: {
      inProgress: 0,
      onHold: 0,
      pending: 0,
      resolved: 0
    }
  }

  constructor(private http: EmployeeHttpService, private notifier: NotifierService) {}

  loadEmployeeTicketStatistics(): void
  {
    this.http.getEmployeeTicketStatistics().subscribe({
      next: result => {
        this.employeeTicketStatistics = result;
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać danych na temat zgłoszeń!");
      }
    })
  }
}

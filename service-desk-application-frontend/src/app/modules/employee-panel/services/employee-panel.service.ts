import { Injectable } from '@angular/core';
import {EmployeeHttpService} from "./employee-http.service";
import {
  EmployeeTicketForm,
  EmployeeTicketTypeStatistics,
  Ticket,
  TicketCategory, TicketStatus, TicketType
} from "../../../models/ticket.interfaces";
import {NotifierService} from "angular-notifier";
import {Observable} from "rxjs";
import {UserAsListElement} from "../../../models/user-data.interfaces";

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

  getTicketCategories(ticketType: string): Observable<TicketCategory[]>
  {
    return this.http.getCategoriesToRegisterTicket(ticketType);
  }

  getCustomers(): Observable<UserAsListElement[]>
  {
    return this.http.getCustomers();
  }

  registerTicketAsEmployee(ticketData: EmployeeTicketForm): Observable<Ticket>
  {
    return this.http.registerTicketAsEmployee(ticketData);
  }

  getUserTickets(ticketType: TicketType, ticketStatus: TicketStatus): Observable<Ticket[]>
  {
    return this.http.getUserTickets(ticketType, ticketStatus);
  }
}

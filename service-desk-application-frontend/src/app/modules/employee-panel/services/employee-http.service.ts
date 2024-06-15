import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {
  EmployeeTicketForm,
  EmployeeTicketTypeStatistics,
  Ticket,
  TicketCategory,
  TicketStatus,
  TicketType
} from "../../../models/ticket.interfaces";
import {ApplicationSettings} from "../../../application-settings";
import {Observable} from "rxjs";
import {UserAsListElement} from "../../../models/user-data.interfaces";

@Injectable({
  providedIn: 'root'
})
export class EmployeeHttpService
{
  constructor(private http: HttpClient) {}

  getEmployeeTicketStatistics(): Observable<EmployeeTicketTypeStatistics>
  {
    return this.http.get<EmployeeTicketTypeStatistics>(ApplicationSettings.apiUrl + "/api/v1/ticket/employee/statistics");
  }

  getCategoriesToRegisterTicket(ticketType: string): Observable<TicketCategory[]>
  {
    if(ticketType === "INCIDENT")
      return this.http.get<TicketCategory[]>(ApplicationSettings.apiUrl + "/api/v1/category/list/active/incidents");
    else if(ticketType == "SERVICE_REQUEST")
      return this.http.get<TicketCategory[]>(ApplicationSettings.apiUrl + "/api/v1/category/list/active/serviceRequests")
    else return new Observable<TicketCategory[]>();
  }

  getCustomers(): Observable<UserAsListElement[]>
  {
    return this.http.get<UserAsListElement[]>(ApplicationSettings.apiUrl + "/api/v1/user/list/active");
  }

  registerTicketAsEmployee(ticketData: EmployeeTicketForm): Observable<Ticket>
  {
    return this.http.post<Ticket>(ApplicationSettings.apiUrl + "/api/v1/ticket/employee/create", ticketData);
  }

  private extractTicketTypeAsString(ticketType: TicketType): string
  {
    let type: string = "";

    switch(ticketType)
    {
      case TicketType.INCIDENT:
        type = "incident";
        break;
      case TicketType.SERVICE_REQUEST:
        type = "service";
        break
      case TicketType.PROBLEM:
        type = "problem";
        break;
    }

    return type;
  }

  private extractTicketStatusAsString(ticketStatus: TicketStatus)
  {
    let status: string = "";

    switch(ticketStatus)
    {
      case TicketStatus.PENDING:
        status = "pending";
        break;
      case TicketStatus.IN_PROGRESS:
        status = "progress";
        break;
      case TicketStatus.ON_HOLD:
        status = "hold";
        break;
      case TicketStatus.RESOLVED:
        status = "resolved";
        break;
      case TicketStatus.CLOSED:
        status = "closed";
        break;
    }

    return status;
  }

  getUserTickets(ticketType: TicketType, ticketStatus: TicketStatus): Observable<Ticket[]>
  {
    const type: string = this.extractTicketTypeAsString(ticketType);
    const status: string = this.extractTicketStatusAsString(ticketStatus);
    return this.http.get<Ticket[]>(ApplicationSettings.apiUrl + "/api/v1/ticket/employee/list/" + type + "/" + status);
  }
}

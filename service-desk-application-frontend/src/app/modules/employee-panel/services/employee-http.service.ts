import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {
  EmployeeTicketForm,
  EmployeeTicketTypeStatistics,
  RegisteredTicket,
  TicketCategory
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

  registerTicketAsEmployee(ticketData: EmployeeTicketForm): Observable<RegisteredTicket>
  {
    return this.http.post<RegisteredTicket>(ApplicationSettings.apiUrl + "/api/v1/ticket/employee/create", ticketData);
  }
}

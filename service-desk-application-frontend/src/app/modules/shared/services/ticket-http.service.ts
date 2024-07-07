import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApplicationSettings} from "../../../application-settings";
import {
  PermissionsInformationAboutTicket,
  TicketDetailsForAnalyst,
  TicketDetailsForEmployee
} from "../../../models/ticket.interfaces";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TicketHttpService
{
  constructor(private http: HttpClient) {}

  getTicketDetails(ticketId: string): Observable<TicketDetailsForEmployee | TicketDetailsForAnalyst>
  {
    return this.http.get<TicketDetailsForEmployee | TicketDetailsForAnalyst>(ApplicationSettings.apiUrl + "/api/v1/ticket/details/" + ticketId);
  }

  getTicketPermissions(ticketId: string): Observable<PermissionsInformationAboutTicket>
  {
    return this.http.get<PermissionsInformationAboutTicket>(ApplicationSettings.apiUrl + "/api/v1/ticket/permissions/" + ticketId);
  }
}

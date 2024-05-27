import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {EmployeeTicketTypeStatistics} from "../../../models/ticket.interfaces";
import {ApplicationSettings} from "../../../application-settings";
import {Observable} from "rxjs";

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
}

import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApplicationSettings} from "../../../application-settings";
import {
  NewTicketStatus,
  PermissionsInformationAboutTicket, TicketCategory,
  TicketDetailsForAnalyst,
  TicketDetailsForEmployee,
  TicketStatus
} from "../../../models/ticket.interfaces";
import {Observable} from "rxjs";
import {ServerResponsesMessage} from "../../../models/server-responses.interfaces";
import {GroupData, Members} from "../../../models/group-data.interfaces";

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

  changeUser(tickedId: string, userId: string)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl +  "/api/v1/ticket/user/change/" + tickedId, {userId: userId});
  }

  changeReporter(tickedId: string, userId: string)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl +  "/api/v1/ticket/reporter/change/" + tickedId, {userId: userId});
  }

  changeStatus(ticketId: string, ticketStatus: NewTicketStatus)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/status/change/" + ticketId, ticketStatus);
  }

  getUserGroups()
  {
    return this.http.get<GroupData[]>(ApplicationSettings.apiUrl + "/api/v1/group/user/list");
  }

  addComment(ticketId: string, comment: string)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/activity/comment/add/" + ticketId, {comment: comment})
  }

  addReminder(ticketId: string, comment: string)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/activity/reminder/add/" + ticketId, {comment: comment})
  }

  addInternalNote(ticketId: string, comment: string)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/activity/note/add/" + ticketId, {comment: comment})
  }

  resolveTicket(ticketId: string, comment: string)
  {
    return this.changeStatus(ticketId, {comment: comment, newTicketStatus: TicketStatus.RESOLVED});
  }

  getCategoriesToRegisterTicket(ticketType: string): Observable<TicketCategory[]>
  {
    if(ticketType === "INCIDENT")
      return this.http.get<TicketCategory[]>(ApplicationSettings.apiUrl + "/api/v1/category/list/active/incidents");
    else if(ticketType == "SERVICE_REQUEST")
      return this.http.get<TicketCategory[]>(ApplicationSettings.apiUrl + "/api/v1/category/list/active/serviceRequests")
    else return new Observable<TicketCategory[]>();
  }

  changeCategory(ticketId: string, categoryId: string)
  {
    return this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/category/change/" + ticketId, {categoryId: categoryId});
  }

  changeDescription(ticketId: string, description: string)
  {
    return this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/description/change/" + ticketId, {description: description});
  }

  getMembersOfGroups(groupId: string)
  {
    return this.http.get<Members>(ApplicationSettings.apiUrl + "/api/v1/group/members/" + groupId);
  }

  changeAssigneeAnalyst(ticketId: string, userId: string)
  {
    return this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/assignee/analyst/change/" + ticketId, {userId: userId});
  }

  getActiveGroups()
  {
    return this.http.get<GroupData[]>(ApplicationSettings.apiUrl + "/api/v1/group/list/active");
  }

  changeGroup(ticketId: string, groupId: string)
  {
    return this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/ticket/assignee/group/change/" + ticketId, {groupId: groupId});
  }
}

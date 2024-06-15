import {GroupData} from "./group-data.interfaces";
import {UserAsListElement} from "./user-data.interfaces";

export interface TicketCategory
{
  categoryId: string,
  name: string,
  description: string,
  categoryActive: boolean,
  ticketType: string,
  defaultGroup: GroupData
}

export interface EmployeeTicketStatusStatistics
{
  pending: number,
  inProgress: number,
  onHold: number,
  resolved: number,
}

export interface EmployeeTicketTypeStatistics
{
  incidents: EmployeeTicketStatusStatistics,
  serviceRequests: EmployeeTicketStatusStatistics
}

export interface EmployeeTicketForm
{
  customer: string,
  category: string,
  description: string
}

export enum TicketType
{
  SERVICE_REQUEST,
  INCIDENT,
  PROBLEM
}

export enum TicketStatus
{
  PENDING,
  IN_PROGRESS,
  ON_HOLD,
  RESOLVED,
  CLOSED
}

export interface Ticket
{
  id: string,
  ticketType: string,
  description: string,
  customer: UserAsListElement,
  reporter: UserAsListElement,
  status: string,
  openDate: string,
  resolveDate: string,
  closeDate: string,
  category: {
    id: string,
    name: string,
    description: string
  }
}

export interface DataOfTicketsForEmployeeList
{
  customer: string,
  openDate: Date;
  category: string
}

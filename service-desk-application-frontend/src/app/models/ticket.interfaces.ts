import {GroupData} from "./group-data.interfaces";

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

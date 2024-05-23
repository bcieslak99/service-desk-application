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

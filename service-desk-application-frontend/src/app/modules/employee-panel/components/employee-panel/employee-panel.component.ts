import {Component, OnInit} from '@angular/core';
import {EmployeePanelService} from "../../services/employee-panel.service";
import {EmployeeTicketTypeStatistics, Ticket, TicketStatus, TicketType} from "../../../../models/ticket.interfaces";
import {NotifierService} from "angular-notifier";

@Component({
  selector: 'app-employee-panel',
  templateUrl: './employee-panel.component.html',
  styleUrls: ['./employee-panel.component.css']
})
export class EmployeePanelComponent implements OnInit
{
  titleOfTicketsPanel: string | null = null;
  ticketsToShow: Ticket[] | null = null;

  constructor(private employeePanelService: EmployeePanelService, private notifier: NotifierService) {}

  getEmployeeTicketStatistics(): EmployeeTicketTypeStatistics
  {
    return this.employeePanelService.employeeTicketStatistics;
  }

  private showTitleOfTicketsPanel(ticketType: TicketType, ticketStatus: TicketStatus): void
  {
    if(ticketType === null || ticketStatus === null) this.titleOfTicketsPanel = null;

    let title = "";

    switch(ticketStatus)
    {
      case TicketStatus.PENDING:
        title += "Oczekujące ";
        break;
      case TicketStatus.ON_HOLD:
        title += "Wstrzymane ";
        break;
      case TicketStatus.IN_PROGRESS:
        title += "Realizowane ";
        break;
      case TicketStatus.RESOLVED:
        title += "Rozwiązane ";
        break;
      case TicketStatus.CLOSED:
        title += "Zamknięte ";
        break;
    }

    if(ticketType === TicketType.INCIDENT) title += "incydenty:";
    else if(ticketType === TicketType.SERVICE_REQUEST) title += "wnioski o usługę:"

    this.titleOfTicketsPanel = title;
  }

  showTickets(ticketType: TicketType, ticketStatus: TicketStatus): void
  {
    this.employeePanelService.loadEmployeeTicketStatistics();

    this.titleOfTicketsPanel = null;
    this.ticketsToShow = null;

    this.employeePanelService.getUserTickets(ticketType, ticketStatus).subscribe({
      next: result => {
        this.ticketsToShow = result;
        this.showTitleOfTicketsPanel(ticketType, ticketStatus);
        console.log(result);
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać listy zgłoszeń!");
      }
    })
  }

  ngOnInit(): void
  {
    this.employeePanelService.loadEmployeeTicketStatistics();
  }

  protected readonly TicketType = TicketType;
  protected readonly TicketStatus = TicketStatus;
}

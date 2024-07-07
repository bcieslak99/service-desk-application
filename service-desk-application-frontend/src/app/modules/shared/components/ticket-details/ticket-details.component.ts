import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {TicketHttpService} from "../../services/ticket-http.service";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {
  ActivityToShowInTable,
  PermissionsInformationAboutTicket,
  TicketDetailsForAnalyst,
  TicketDetailsForEmployee
} from "../../../../models/ticket.interfaces";
import {Subscription, switchMap} from "rxjs";
import {NotifierService} from "angular-notifier";
import {AuthService} from "../../../../services/auth.service";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {UserAsListElement} from "../../../../models/user-data.interfaces";

@Component({
  selector: 'app-ticket-details',
  templateUrl: './ticket-details.component.html',
  styleUrls: ['./ticket-details.component.css']
})
export class TicketDetailsComponent implements AfterViewInit, OnDestroy
{
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    ticketDetails: TicketDetailsForEmployee | TicketDetailsForAnalyst | null = null;
    permissionsToTicket: PermissionsInformationAboutTicket | null = null;
    subForDetails!: Subscription;
    subFoPermissions!: Subscription;
    activitiesColumns: string[] = ["activityDate", "ticketActivityType", "analyst", "description"]
    listOfActivities: MatTableDataSource<ActivityToShowInTable> = new MatTableDataSource<ActivityToShowInTable>();

    constructor(private http: TicketHttpService, private route: ActivatedRoute, private notifier: NotifierService, private auth: AuthService) {}

    private loadTicketDetails(): void
    {
        this.subForDetails = this.route.paramMap.pipe(switchMap((params: ParamMap) => {
         return this.http.getTicketDetails(params.get("id") as string);
      })).subscribe({
          next: result => {
              this.ticketDetails = result;
              let activities: ActivityToShowInTable[] = [];

              this.ticketDetails.activities.forEach(activity => {
                let activityToShow: ActivityToShowInTable = {
                  analyst: `${activity.analyst.surname} ${activity.analyst.name} (${activity.analyst.mail})`,
                  activityDate: activity.activityDate,
                  description: activity.description,
                  ticketActivityType: this.getActivityType(activity.ticketActivityType)
                }

                activities.push(activityToShow);
              });

              this.listOfActivities = new MatTableDataSource<ActivityToShowInTable>(activities);
              setTimeout(() => this.preparePaginator());
          },
          error: err => {
            this.notifier.notify("error", "Nie udało się pobrać danych na temat zgłoszenia");
          }
      });
    }

    private loadPermissionsToTicket(): void
    {
        this.subFoPermissions = this.route.paramMap.pipe(switchMap((params: ParamMap) => {
            return this.http.getTicketPermissions(params.get("id") as string);
        })).subscribe({
            next: result => {
                this.permissionsToTicket = result;
            }
        });
    }

    userIsEmployee(): boolean
    {
      return this.auth.userIsEmployee();
    }

    userIsFirstLineAnalyst(): boolean
    {
      return this.permissionsToTicket?.accessAsFirstLineAnalyst !== undefined ? this.permissionsToTicket?.accessAsFirstLineAnalyst : false;
    }

    userIsSecondLineAnalyst(): boolean
    {
      return this.permissionsToTicket?.accessAsSecondLineAnalyst !== undefined ? this.permissionsToTicket?.accessAsSecondLineAnalyst : false;
    }

    getReporter(): string
    {
      const reporter = this.ticketDetails?.reporter;
      if(reporter !== undefined && reporter !== null) return `${reporter.surname} ${reporter.name} (${reporter.mail})`;
      else return "";
    }

    getCustomer(): string
    {
      const customer = this.ticketDetails?.customer;
      if(customer !== undefined && customer !== null) return `${customer.surname} ${customer.name} (${customer.mail})`;
      else return "";
    }

    getActivityType(activity: string): string
    {
      let type: string = "";

      switch(activity)
      {
        case "CREATE_TICKET":
          type = "Utworzenie zgłoszenia";
          break;
        case "INTERNAL_NOTE":
          type = "Notatka wewnętzna";
          break;
        case "ASSIGN_INDIVIDUAL":
          type = "Przypisanie osoby";
          break;
        case "ASSIGN_GROUP":
          type = "Przypisanie grupy";
          break;
        case "RESOLUTION":
          type = "Rozwiązanie";
          break;
        case "COMPLAINT":
          type = "Monit od użytkownika";
          break;
        case "CLOSE_TICKET":
          type = "Automatyczne zamknięcie";
          break;
        default:
          type = "Nieokreślone";
          break;
      }

      return type;
    }

    getTicketStatus(): string
    {
      const status = this.ticketDetails?.status;
      if(status === undefined) return "";
      let statusToShow: string = "";

      switch (status)
      {
        case "PENDING":
          statusToShow = "Oczekujące";
          break
        case "ON_HOLD":
          statusToShow = "Wstrzymane";
          break;
        case "IN_PROGRESS":
          statusToShow = "Realizowane";
          break;
        case "RESOLVED":
          statusToShow = "Rozwiązane";
          break;
        case "CLOSED":
          statusToShow = "Zamknięte";
          break;
      }

      return statusToShow;
    }

    ticketIsClosed(): boolean
    {
      return this.getTicketStatus() === "Zamknięte";
    }

    getAssignedAnalyst()
    {
      let analyst: UserAsListElement | null;

      if((this.ticketDetails as TicketDetailsForAnalyst).assigneeAnalyst === null || (this.ticketDetails as TicketDetailsForAnalyst).assigneeAnalyst === undefined) analyst = null;
      else analyst = (this.ticketDetails as TicketDetailsForAnalyst).assigneeAnalyst;

      if(analyst === null) return "Brak osoby realizującej";
      else return `${analyst.surname} ${analyst.name} (${analyst.mail})`;
    }

    preparePaginator(): void
    {
      this.listOfActivities.paginator = this.paginator;
      this.listOfActivities.sort = this.sort;
      this.paginator._intl.itemsPerPageLabel = "Elementów na stronę: ";
      this.paginator._intl.nextPageLabel = "Następna strona";
      this.paginator._intl.previousPageLabel = "Poprzednia strona";

      this.paginator._intl.getRangeLabel = (page: number, pageSize: number, length: number) =>
      {
        if (length == 0 || pageSize == 0)
        {
          return `0 z ${length}`;
        }

        length = Math.max(length, 0);
        const startIndex = page * pageSize;
        const endIndex = startIndex < length ?
          Math.min(startIndex + pageSize, length) :
          startIndex + pageSize;
        return `${startIndex + 1} – ${endIndex} z ${length}`;
      }
    }

    applyFilter(event: Event): void
    {
      const filterValue: string = (event.target as HTMLInputElement).value;
      this.listOfActivities.filter = filterValue.trim().toLowerCase();

      if(this.listOfActivities.paginator)
        this.listOfActivities.paginator.firstPage();
    }

    ngAfterViewInit(): void
    {
      this.loadTicketDetails();
      this.loadPermissionsToTicket();
    }

    ngOnDestroy(): void
    {
        this.subForDetails.unsubscribe();
        this.subFoPermissions.unsubscribe();
    }
}

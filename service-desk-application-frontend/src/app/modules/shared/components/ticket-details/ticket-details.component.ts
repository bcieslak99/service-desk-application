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
import {MatDialog} from "@angular/material/dialog";
import {SelectingUserDialogComponent} from "../dialogs/selecting-user-dialog/selecting-user-dialog.component";
import {UserHttpService} from "../../services/user-http.service";
import {SelectingStatusDialogComponent} from "../dialogs/selecting-status-dialog/selecting-status-dialog.component";
import {GroupData} from "../../../../models/group-data.interfaces";
import {CommentDialogComponent} from "../dialogs/comment-dialog/comment-dialog.component";
import {
  SelectingCategoryDialogComponent
} from "../dialogs/selecting-category-dialog/selecting-category-dialog.component";
import {SelectingGroupDialogComponent} from "../dialogs/selecting-group-dialog/selecting-group-dialog.component";

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
    ticketExists: boolean | null = null;
    userGroups: GroupData[] = [];

    constructor
    (
      private ticketHttp: TicketHttpService,
      private userHttp: UserHttpService,
      private route: ActivatedRoute,
      private notifier: NotifierService,
      private auth: AuthService,
      private dialog: MatDialog
    ) {}

    private loadTicketDetails(): void
    {
        this.subForDetails = this.route.paramMap.pipe(switchMap((params: ParamMap) => {
         return this.ticketHttp.getTicketDetails(params.get("id") as string);
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
              this.ticketExists = true;
          },
          error: err => {
            this.notifier.notify("error", "Nie udało się pobrać danych na temat zgłoszenia");
            this.ticketExists = false;
          }
      });
    }

    private loadPermissionsToTicket(): void
    {
        this.subFoPermissions = this.route.paramMap.pipe(switchMap((params: ParamMap) => {
            return this.ticketHttp.getTicketPermissions(params.get("id") as string);
        })).subscribe({
            next: result => {
                this.permissionsToTicket = result;
            }
        });
    }

    private loadUserGroups(): void
    {
      this.ticketHttp.getUserGroups().subscribe({
        next: result => {
          this.userGroups = result;
        }
      })
    }

    userIsMemberOfAssigneeGroup(): boolean
    {
      return this.userGroups.filter(group => group.name === (this.ticketDetails as TicketDetailsForAnalyst).assigneeGroup.name).length > 0;
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

    showTicketDescription(): string
    {
      if(this.ticketDetails?.description !== undefined)
      {
        let description: string = this.ticketDetails?.description
        return description.replace(/\n/g, '<br>');
      }
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
        case "EDIT":
          type = "Edycja";
          break;
        case "CHANGE_STATUS":
          type = "Zmiana statusu"
          break;
        case "COMMENT":
          type = "Komentarz";
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

    ticketIsResolved(): boolean
    {
      return this.getTicketStatus() === "Rozwiązane";
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

    getAssigneeGroup(): string
    {
      let group: GroupData | null = null;
      group = (this.ticketDetails as TicketDetailsForAnalyst).assigneeGroup;
      return group.name;
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

    openDialogToChangeUser(): void
    {
      this.userHttp.getActiveUsers().subscribe({
        next: result =>  {
          const changeUserDialog = this.dialog.open(SelectingUserDialogComponent, {
            data: result
          });

          changeUserDialog.afterClosed().subscribe(result => {
            if(result !== null && result !== undefined && this.ticketDetails?.customer.userId !== result.userId)
            {
              if(this.ticketDetails?.id !== undefined)
              {
                this.ticketHttp.changeUser(this.ticketDetails.id, result.userId).subscribe({
                  next: value => {
                    this.loadTicketDetails();
                    this.loadPermissionsToTicket();
                    this.notifier.notify("success", value.message);
                  },
                  error: err => {
                    this.notifier.notify("error", err.message);
                  }
                });
              }
            }
            else if(result !== undefined && this.ticketDetails?.customer.userId === result.userId) this.notifier.notify("warning", "Ten użytkownik jest już przypisany do zgłoszenia!");
          });
        },
        error: err =>  {
          this.notifier.notify("error", "Nie udało się pobrać listy użytkowników!");
        }
      });
    }

    openDialogToChangeReporter(): void
    {
      this.userHttp.getActiveUsers().subscribe({
        next: result =>  {
          const changeReporterDialog = this.dialog.open(SelectingUserDialogComponent, {
            data: result
          });

          changeReporterDialog.afterClosed().subscribe(result => {
            if(result !== null && result !== undefined && this.ticketDetails?.reporter.userId !== result.userId)
            {
              if(this.ticketDetails?.id !== undefined)
              {
                this.ticketHttp.changeReporter(this.ticketDetails.id, result.userId).subscribe({
                  next: value => {
                    this.loadTicketDetails();
                    this.loadPermissionsToTicket();
                    this.notifier.notify("success", value.message);
                  },
                  error: err => {
                    this.notifier.notify("error", err.message);
                  }
                });
              }
            }
            else if(result !== undefined && this.ticketDetails?.customer.userId === result.userId) this.notifier.notify("warning", "Ten zgłaszający jest już przypisany do zgłoszenia!");
          })
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się pobrać listy użytkowników!");
        }
      });

      if(this.permissionsToTicket !== null && (this.permissionsToTicket?.accessAsEmployee === false ||
        !this.permissionsToTicket.accessAsFirstLineAnalyst || !this.permissionsToTicket.accessAsSecondLineAnalyst)) this.auth.redirectUser();
    }

    openDialogToChangeStatus(): void
    {
      const changeStatusDialog = this.dialog.open(SelectingStatusDialogComponent);

      changeStatusDialog.afterClosed().subscribe(result => {
        if(result !== null && result !== undefined && this.ticketDetails?.status !== result.newTicketStatus)
        {
          if(this.ticketDetails?.id !== undefined)
          {
            this.ticketHttp.changeStatus(this.ticketDetails.id, result).subscribe({
              next: value => {
                this.loadTicketDetails();
                this.loadPermissionsToTicket();
                this.notifier.notify("success", value.message);
              },
              error: err => {
                this.notifier.notify("error", err.message);
              }
            });
          }
        }
      });
    }

    openDialogToAddComment(): void
    {
      const commentDialog = this.dialog.open(CommentDialogComponent, {data: {title: "Dodaj komentarz", comment: ""}});

      commentDialog.afterClosed().subscribe(result => {
        if(result !== null && result !== undefined && this.ticketDetails !== undefined && this.ticketDetails?.id !== undefined)
        {
          this.ticketHttp.addComment(this.ticketDetails?.id, result).subscribe({
            next: value => {
              this.loadTicketDetails();
              this.loadPermissionsToTicket();
              this.notifier.notify("success", value.message);
            },
            error: err => {
              this.notifier.notify("error", "Nie udało się dodać komentarza do zgłoszenia");
            }
          });
        }
      });
    }

    openDialogToAddInternalNote(): void
    {
      const internalNoteDialog = this.dialog.open(CommentDialogComponent, {data: {title: "Dodaj notatkę wewnętrzną", comment: ""}})

      internalNoteDialog.afterClosed().subscribe(result => {
        if(result !== null && result !== undefined && this.ticketDetails !== undefined && this.ticketDetails?.id !== undefined)
        {
          this.ticketHttp.addInternalNote(this.ticketDetails?.id, result).subscribe({
            next: value => {
              this.loadTicketDetails();
              this.loadPermissionsToTicket();
              this.notifier.notify("success", value.message);
            },
            error: err => {
              this.notifier.notify("error", "Nie udało się dodać notatki do zgłoszenia");
            }
          });
        }
      });
    }

    openDialogToAddReminder(): void
    {
      const reminderDialog = this.dialog.open(CommentDialogComponent, {data: {title: "Dodaj monit od użytkownika", comment: ""}})

      reminderDialog.afterClosed().subscribe(result => {
        if(result !== null && result !== undefined && this.ticketDetails !== undefined && this.ticketDetails?.id !== undefined)
        {
          this.ticketHttp.addReminder(this.ticketDetails?.id, result).subscribe({
            next: value => {
              this.loadTicketDetails();
              this.loadPermissionsToTicket();
              this.notifier.notify("success", value.message);
            },
            error: err => {
              this.notifier.notify("error", "Nie udało się dodać monitu do zgłoszenia");
            }
          });
        }
      });
    }

    resolveTicket(): void
    {
      const resolveDialog = this.dialog.open(CommentDialogComponent, {data: {title: "Rozwiązanie zgłoszenia", comment: ""}});

      resolveDialog.afterClosed().subscribe(result => {
        if(result !== null && result !== undefined && this.ticketDetails !== undefined && this.ticketDetails?.id !== undefined)
        {
          this.ticketHttp.resolveTicket(this.ticketDetails.id, result).subscribe({
            next: value =>  {
              this.loadTicketDetails();
              this.loadPermissionsToTicket();
              this.notifier.notify("success", value.message);
            },
            error: err =>  {
              this.notifier.notify("error", err.message)
            }
          });
        }
      });
    }

    changeCategory(): void
    {
      if(this.ticketDetails !== undefined && this.ticketDetails?.ticketType !== undefined)
      {
        this.ticketHttp.getCategoriesToRegisterTicket(this.ticketDetails?.ticketType).subscribe({
          next: categories => {
            const changeCategoryDialog = this.dialog.open(SelectingCategoryDialogComponent, {data: categories});

            changeCategoryDialog.afterClosed().subscribe(result => {
              if(result !== null && result !== undefined && this.ticketDetails !== undefined && this.ticketDetails?.id !== undefined)
              {
                this.ticketHttp.changeCategory(this.ticketDetails.id, result.categoryId).subscribe({
                  next: value => {
                    this.loadTicketDetails();
                    this.loadPermissionsToTicket();
                    this.notifier.notify("success", value.message);
                  },
                  error: err => {
                    this.notifier.notify("error", err.message);
                  }
                });
              }
            });
          },
          error: err => {
            this.notifier.notify("error", "Nie udało się pobrać listy kategorii");
          }
        });
      }
    }

    openDialogToEditDescription(): void
    {
      const dialogToChangeDescription = this.dialog.open(CommentDialogComponent, {data: {title: "Edytuj opis zgłoszenia", comment: this.ticketDetails?.description}});

      dialogToChangeDescription.afterClosed().subscribe(result => {
        if(result !== null && result !== undefined && this.ticketDetails !== undefined && this.ticketDetails?.id !== undefined)
        {
          this.ticketHttp.changeDescription(this.ticketDetails.id, result).subscribe({
            next: value => {
              this.loadTicketDetails();
              this.loadPermissionsToTicket();
              this.notifier.notify("success", value.message);
            },
            error: err => {
              this.notifier.notify("error", err.message);
            }
          });
        }
      });
    }

    openDialogToChangeAssigneePerson(): void
    {
      this.ticketHttp.getMembersOfGroups((this.ticketDetails as TicketDetailsForAnalyst).assigneeGroup.groupId).subscribe({
        next: users => {
          const changeAssigneePersonDialog = this.dialog.open(SelectingUserDialogComponent, {data: users.members});

          changeAssigneePersonDialog.afterClosed().subscribe(result => {
            if(result !== null && result !== undefined && this.ticketDetails !== null)
            {
              console.log(result.id);

              this.ticketHttp.changeAssigneeAnalyst(this.ticketDetails.id, result.id).subscribe({
                next: value => {
                  this.loadTicketDetails();
                  this.loadPermissionsToTicket();
                  this.notifier.notify("success", value.message);
                },
                error: err => {
                  this.notifier.notify("error", err.message);
                }
              });
            }
          });
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się pobrać listy użytkowników.");
        }
      });
    }

    openDialogToChangeGroup(): void
    {
      this.ticketHttp.getActiveGroups().subscribe({
        next: groups => {
          const dialogToChangeGroup = this.dialog.open(SelectingGroupDialogComponent, {data: groups});

          dialogToChangeGroup.afterClosed().subscribe(result => {
            if(result !== undefined && result !== null && this.ticketDetails !== null)
            {
              this.ticketHttp.changeGroup(this.ticketDetails.id, result.groupId).subscribe({
                next: value => {
                  this.loadTicketDetails();
                  this.loadPermissionsToTicket();
                  this.notifier.notify("success", value.message);
                  setTimeout(() => {
                    if(this.permissionsToTicket?.accessAsEmployee === false &&
                      !this.permissionsToTicket.accessAsFirstLineAnalyst &&
                      !this.permissionsToTicket.accessAsSecondLineAnalyst)
                      this.auth.redirectUser();
                  });
                },
                error: err =>  {
                  this.notifier.notify("error", err.message);
                }
              });
            }
          })
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się pobrać listy aktywnych grup wsparcia!");
        }
      });
    }

    ngAfterViewInit(): void
    {
      this.loadTicketDetails();
      this.loadPermissionsToTicket();
      this.loadUserGroups();
    }

    ngOnDestroy(): void
    {
        this.subForDetails.unsubscribe();
        this.subFoPermissions.unsubscribe();
    }
}

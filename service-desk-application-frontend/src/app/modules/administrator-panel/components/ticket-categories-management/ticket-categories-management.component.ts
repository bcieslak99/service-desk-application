import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NotifierService} from "angular-notifier";
import {MatDialog} from "@angular/material/dialog";
import {ApplicationSettings} from "../../../../application-settings";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {TicketCategory} from "../../../../models/ticket.interfaces";
import {MatTableDataSource} from "@angular/material/table";
import {ServerResponsesMessage} from "../../../../models/server-responses.interfaces";
import {NewCategoryDialogComponent} from "../dialogs/new-category-dialog/new-category-dialog.component";

@Component({
  selector: 'app-ticket-categories-management',
  templateUrl: './ticket-categories-management.component.html',
  styleUrls: ['./ticket-categories-management.component.css']
})
export class TicketCategoriesManagementComponent implements AfterViewInit
{
  categories: MatTableDataSource<TicketCategory> = new MatTableDataSource<TicketCategory>();
  displayedColumns: string[] = ["name", "description", "ticketType", "defaultGroup", "categoryActive", "actions"];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private http: HttpClient, private notifier: NotifierService, private dialog: MatDialog) {}

  preparePaginator(): void
  {
    this.categories.paginator = this.paginator;
    this.categories.sort = this.sort;
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

  loadCategories(): void
  {
    this.http.get<TicketCategory[]>(ApplicationSettings.apiUrl + "/api/v1/category/list/all").subscribe({
      next: categories => {
        categories.forEach(element => {
          switch(element.ticketType)
          {
            case "SERVICE_REQUEST":
              element.ticketType = "Wniosek o usługę";
              break;
            case "INCIDENT":
              element.ticketType = "Incydent";
              break;
            case "PROBLEM":
              element.ticketType = "Problem";
              break
          }
        });

        this.categories = new MatTableDataSource(categories);
        this.preparePaginator();
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać kategorii!");
      }
    })
  }

  ngAfterViewInit(): void
  {
    this.loadCategories();
  }

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.categories.filter = filterValue.trim().toLowerCase();

    if(this.categories.paginator)
      this.categories.paginator.firstPage();
  }

  activateCategory(categoryId: string): void
  {
    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/category/activate/" + categoryId, {}).subscribe({
      next: result => {
        this.notifier.notify(result.code.toLowerCase(), result.message);
        this.loadCategories();
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
      }
    })
  }

  deactivateCategory(categoryId: string): void
  {
    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/category/deactivate/" + categoryId, {}).subscribe({
      next: result => {
        this.notifier.notify(result.code.toLowerCase(), result.message);
        this.loadCategories();
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
      }
    })
  }

  openDialogToCreateNewTicketCategory(): void
  {
    const dialogToCreateCategory = this.dialog.open(NewCategoryDialogComponent);

    dialogToCreateCategory.afterClosed().subscribe(result => {
      if(result) this.loadCategories();
    })
  }
}

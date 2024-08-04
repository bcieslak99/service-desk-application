import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {Observable} from "rxjs";
import {TicketDetailsForAnalyst} from "../../../../models/ticket.interfaces";
import {MatTableDataSource} from "@angular/material/table";
import {NotifierService} from "angular-notifier";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-ticket-list',
  templateUrl: './ticket-list.component.html',
  styleUrls: ['./ticket-list.component.css']
})
export class TicketListComponent implements AfterViewInit
{
  @Input() ticketsRequest: Observable<TicketDetailsForAnalyst[]> | null = null;
  dataSource: MatTableDataSource<TicketDetailsForAnalyst> = new MatTableDataSource<TicketDetailsForAnalyst>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  displayedColumns: string[] = ["customer", "openDate", "category", "action"];
  @Input() title: string = "";

  constructor(private notifier: NotifierService) {}

  preparePaginator(): void
  {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
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

  getData(): void
  {
    if(this.ticketsRequest !== null)
    {
      this.ticketsRequest.subscribe({
        next: tickets =>  {
          this.dataSource = new MatTableDataSource<TicketDetailsForAnalyst>(tickets);
          this.preparePaginator();
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się pobrać danych na temat zgłoszeń!");
        }
      });
    }
  }

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if(this.dataSource.paginator)
      this.dataSource.paginator.firstPage();
  }

  ngAfterViewInit(): void
  {
    this.preparePaginator();
    this.getData();
  }
}

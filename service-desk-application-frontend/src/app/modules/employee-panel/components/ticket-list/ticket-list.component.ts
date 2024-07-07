import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {DataOfTicketsForEmployeeList, Ticket} from "../../../../models/ticket.interfaces";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {Router,} from "@angular/router";

@Component({
  selector: 'app-ticket-list',
  templateUrl: './ticket-list.component.html',
  styleUrls: ['./ticket-list.component.css']
})
export class TicketListComponent implements AfterViewInit
{
  @Input() title: string = "Zgłoszenia";
  @Input() tickets: Ticket[] = [];
  displayedColumns: string[] = ["customer", "openDate", "category", "action"];
  listOfTickets: MatTableDataSource<DataOfTicketsForEmployeeList> = new MatTableDataSource<DataOfTicketsForEmployeeList>();
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private router: Router) {}

  prepareData(): void
  {
    let list: DataOfTicketsForEmployeeList[] = [];

    this.tickets.forEach(element => {
      list.push({
        category: element.category.name,
        customer: element.customer.surname + " " + element.customer.name + " (" + element.customer.mail + ")",
        openDate: new Date(element.openDate),
        id: element.id
      })
    });

    this.listOfTickets = new MatTableDataSource(list);
    this.preparePaginator();
  }

  preparePaginator(): void
  {
    this.listOfTickets.paginator = this.paginator;
    this.listOfTickets.sort = this.sort;
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
    this.listOfTickets.filter = filterValue.trim().toLowerCase();

    if(this.listOfTickets.paginator)
      this.listOfTickets.paginator.firstPage();
  }

  ngAfterViewInit(): void
  {
    this.prepareData();
  }
}

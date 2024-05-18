import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NotifierService} from "angular-notifier";
import {MatDialog} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {GroupData} from "../../../../models/group-data.interfaces";
import {ApplicationSettings} from "../../../../application-settings";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-support-groups-management',
  templateUrl: './support-groups-management.component.html',
  styleUrls: ['./support-groups-management.component.css']
})
export class SupportGroupsManagementComponent implements AfterViewInit
{
  filter: string = "";
  displayedColumns: string[] = ["name", "description", "groupType", "active", "activities"];
  listOfGroups: MatTableDataSource<GroupData> = new MatTableDataSource<GroupData>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private http: HttpClient, private notifier: NotifierService, private dialog: MatDialog) {}

  private getGroupsList() : Observable<GroupData[]>
  {
    return this.http.get<GroupData[]>(ApplicationSettings.apiUrl + "/api/v1/group/list");
  }

  preparePaginator(): void
  {
    this.listOfGroups.paginator = this.paginator;
    this.listOfGroups.sort = this.sort;
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

  loadGroups(): void
  {
    this.getGroupsList().subscribe({
      next: value => {
        this.listOfGroups = new MatTableDataSource<GroupData>(value);
        this.filter = "";
        this.preparePaginator();
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać listy grup wsparcia!");
      }
    });
  }

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.listOfGroups.filter = filterValue.trim().toLowerCase();

    if(this.listOfGroups.paginator)
      this.listOfGroups.paginator.firstPage();
  }

  ngAfterViewInit(): void
  {
    this.loadGroups();
  }
}

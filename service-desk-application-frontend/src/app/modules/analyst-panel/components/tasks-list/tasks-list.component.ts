import {AfterViewInit, Component, EventEmitter, Output, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NotifierService} from "angular-notifier";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {TaskSetAsListElement} from "../../../../models/tasks.interfaces";
import {ApplicationSettings} from "../../../../application-settings";

@Component({
  selector: 'app-tasks-list',
  templateUrl: './tasks-list.component.html',
  styleUrls: ['./tasks-list.component.css']
})
export class TasksListComponent implements AfterViewInit
{
  dataSource: MatTableDataSource<TaskSetAsListElement> = new MatTableDataSource<TaskSetAsListElement>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  displayedColumns: string[] = ["title", "group", "plannedEndDate", "action"];
  @Output() showTaskSet: EventEmitter<string> = new EventEmitter<string>();

  constructor(private http: HttpClient, private notifier: NotifierService) {}

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if(this.dataSource.paginator)
      this.dataSource.paginator.firstPage();
  }

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

  loadTaskSetList(): void
  {
    this.http.get<TaskSetAsListElement[]>(ApplicationSettings.apiUrl + "/api/v1/task/list").subscribe({
      next: taskSets => {
        this.dataSource = new MatTableDataSource<TaskSetAsListElement>(taskSets);
        this.preparePaginator();
      },
      error: err =>  {
        this.notifier.notify("error", "Nie udało się pobrać zbiorów zadań!");
      }
    });
  }

  showTaskSetDetails(taskSetId: string): void
  {
    this.showTaskSet.emit(taskSetId);
  }

  ngAfterViewInit(): void
  {
    this.loadTaskSetList();
  }
}

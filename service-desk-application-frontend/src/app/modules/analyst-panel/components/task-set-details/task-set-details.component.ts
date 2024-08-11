import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NotifierService} from "angular-notifier";
import {Task, TaskSet} from "../../../../models/tasks.interfaces";
import {ApplicationSettings} from "../../../../application-settings";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";
import {ServerResponsesMessage} from "../../../../models/server-responses.interfaces";

@Component({
  selector: 'app-task-set-details',
  templateUrl: './task-set-details.component.html',
  styleUrls: ['./task-set-details.component.css']
})
export class TaskSetDetailsComponent implements AfterViewInit
{
  @Input() taskSetId: string | null = null;
  taskSet: TaskSet | null = null;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  displayedColumns: string[] = ["description", "action"];
  dataSource: MatTableDataSource<Task> = new MatTableDataSource<Task>([]);
  comment: string = "";

  constructor(private http: HttpClient, private notifier: NotifierService) {}

  loadTaskSet(): void
  {
    if(this.taskSetId === null) return;

    this.http.get<TaskSet>(ApplicationSettings.apiUrl + "/api/v1/task/" + this.taskSetId).subscribe({
      next: taskSet => {
        this.taskSet = taskSet;
        this.dataSource = new MatTableDataSource(this.taskSet.tasks);
        this.preparePaginator();
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać danych na temat zbioru zadań!");
      }
    });
  }

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

  setTaskAsDone(taskId: string): void
  {
    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/task/done/" + taskId, {}).subscribe({
      next: result => {
        this.loadTaskSet();
        this.notifier.notify("success", "Zadanie zostało zrealizowane");
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się oznaczyć zadania jako zrealizowane!");
      }
    });
  }

  commentButtonIsActive(): boolean
  {
    return this.comment.trim().length >= 3;
  }

  addComment(): void
  {
    this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/task/comment/" + this.taskSet?.id, {comment: this.comment}).subscribe({
      next: result => {
        this.loadTaskSet();
        this.comment = "";
        this.notifier.notify("success", "Komentarz został dodany.");
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się dodać komentarza!");
      }
    });
  }

  showText(text: string): string
  {
    return text.replace(/\n/g, '<br>');
  }

  ngAfterViewInit(): void
  {
    this.loadTaskSet();
  }
}

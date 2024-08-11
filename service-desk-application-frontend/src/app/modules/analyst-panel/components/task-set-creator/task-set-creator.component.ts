import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NotifierService} from "angular-notifier";
import {TaskForm, TaskSetForm} from "../../../../models/tasks.interfaces";
import {GroupData} from "../../../../models/group-data.interfaces";
import {ApplicationSettings} from "../../../../application-settings";
import {ServerResponsesMessage} from "../../../../models/server-responses.interfaces";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-task-set-creator',
  templateUrl: './task-set-creator.component.html',
  styleUrls: ['./task-set-creator.component.css']
})
export class TaskSetCreatorComponent implements AfterViewInit
{
  title: string = "";
  plannedEndDate: Date | null = null;
  taskDescription: string = "";
  tasks: string[] = [];
  supportGroup: string = "";
  groups: GroupData[] = [];
  dataSource: MatTableDataSource<string> = new MatTableDataSource<string>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  columns: string[] = ["task", "action"];

  constructor(private http: HttpClient, private notifier: NotifierService) {}

  activeButtonToAddTask(): boolean
  {
    return this.taskDescription.trim().length > 3 && this.tasks.filter(element => element === this.taskDescription).length < 1;
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

  addTask(): void
  {
    this.tasks.push(this.taskDescription);
    this.dataSource = new MatTableDataSource(this.tasks);
    this.preparePaginator();
    this.taskDescription = "";
  }

  removeTask(task: string): void
  {
    this.tasks = this.tasks.filter(element => !(element === task));
    this.dataSource = new MatTableDataSource(this.tasks);
    this.preparePaginator();
  }

  loadGroups(): void
  {
    this.http.get<GroupData[]>(ApplicationSettings.apiUrl + "/api/v1/group/list/active/manager").subscribe({
      next: groups => {
        this.groups = groups;
        if(groups.length > 0) this.supportGroup = groups[0].groupId;
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać listy grup!");
      }
    });
  }

  clearAll(): void
  {
    this.title = "";
    this.plannedEndDate = null;
    this.taskDescription = "";
    this.tasks = [];
    this.supportGroup = "";
    this.dataSource = new MatTableDataSource<string>([]);
    this.loadGroups();
  }

  taskSetCanBeCreated(): boolean
  {
    return this.title.trim().length > 3 && this.plannedEndDate !== null && this.tasks.length > 0 && this.supportGroup.trim().length > 0;
  }

  createTaskSet(): void
  {
    if(this.plannedEndDate === null) return;

    let tasks: TaskForm[] = [];
    let i: number = 1;

    this.tasks.forEach(task => {
      tasks.push({description: task, position: i});
      i++;
    });

    let taskSet: TaskSetForm = {
      title: this.title,
      plannedEndDate: this.plannedEndDate,
      supportGroupId: this.supportGroup,
      tasks: tasks
    };

    this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/task/create", taskSet).subscribe({
      next: result =>  {
        this.clearAll();
        this.notifier.notify("success", "Zbiór zadań został utworzony. Przejdź do zakładki z zadaniami w menu bocznym.")
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się utworzyć zbioru zadań!");
      }
    });
  }

  ngAfterViewInit(): void
  {
    this.loadGroups();
    this.preparePaginator();
  }
}

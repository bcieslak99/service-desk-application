import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {ApplicationSettings} from "../../../../../application-settings";
import {GroupData} from "../../../../../models/group-data.interfaces";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";
import {MatTableDataSource} from "@angular/material/table";
import {UserAsListElement} from "../../../../../models/user-data.interfaces";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-group-manager-dialog',
  templateUrl: './group-manager-dialog.component.html',
  styleUrls: ['./group-manager-dialog.component.css']
})
export class GroupManagerDialogComponent implements OnInit
{
  filter: string = "";
  private groupId!: string;
  groupDetails: GroupData | null = null;

  users: MatTableDataSource<UserAsListElement> = new MatTableDataSource<UserAsListElement>();
  columnsToShow: string[] = ["surname", "name", "mail", "action"];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(public dialog: MatDialogRef<GroupManagerDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private notifier: NotifierService, private http: HttpClient) {}

  closeDialog(): void
  {
    this.dialog.close();
  }

  private closeDialogAfterError(error: any): void
  {
    let result: ServerResponsesMessage = error.error as ServerResponsesMessage;
    this.notifier.notify("error", result.message);
    this.closeDialog();
  }

  private loadData(): void
  {
    this.http.get<GroupData>(ApplicationSettings.apiUrl + "/api/v1/group/" + this.groupId).subscribe({
      next: groupDetails => {
        this.groupDetails = groupDetails;

        this.http.get<UserAsListElement[]>(ApplicationSettings.apiUrl + "/api/v1/user/list/active").subscribe({
          next: users => {
            users = users.filter(element => {
              if(this.groupDetails === null || this.groupDetails.manager === null) return true;
              else return this.groupDetails.manager.userId !== element.userId;
            });

            this.users = new MatTableDataSource<UserAsListElement>(users);
            this.preparePaginator();
          },
          error: err => this.closeDialogAfterError(err)
        })
      },
      error: err => this.closeDialogAfterError(err)
    })
  }

  changeManager(managerId: string | null): void
  {
    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/group/manager/set/" + this.groupId, {userId: managerId})
      .subscribe({
        next: value => {
          this.loadData();
        },
        error: err =>  {
          let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
          this.notifier.notify("error", result.message);
        }
      })
  }

  ngOnInit(): void
  {
    this.groupId = this.data;
    this.loadData();
  }

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.users.filter = filterValue.trim().toLowerCase();

    if(this.users.paginator)
      this.users.paginator.firstPage();
  }

  preparePaginator(): void
  {
    this.users.paginator = this.paginator;
    this.users.sort = this.sort;
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
}

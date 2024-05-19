import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {ApplicationSettings} from "../../../../../application-settings";
import {GroupData, GroupMembers, MemberToModify} from "../../../../../models/group-data.interfaces";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'app-group-members-management-dialog',
  templateUrl: './group-members-management-dialog.component.html',
  styleUrls: ['./group-members-management-dialog.component.css']
})
export class GroupMembersManagementDialogComponent implements OnInit
{
  filter: string = "";
  groupDetails?: GroupData;
  private groupId!: string;
  membersToShow!: MatTableDataSource<MemberToModify>;

  columnsToShow: string[] = ["surname", "name", "mail", "action"]

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(public dialog: MatDialogRef<GroupMembersManagementDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private notifier: NotifierService, private http: HttpClient) {}

  closeDialog(): void
  {
    this.dialog.close();
  }

  preparePaginator(): void
  {
    this.membersToShow.paginator = this.paginator;
    this.membersToShow.sort = this.sort;
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

  private showLoadingError(error: any): void
  {
    let result: ServerResponsesMessage = error.error as ServerResponsesMessage;
    this.notifier.notify("error", result.message);
    this.closeDialog();
  }

  private prepareTable(members: GroupMembers): void
  {
    let membersToShow: MemberToModify[] = [];

    members.addedUsers.forEach(element => membersToShow.push({
      mail: element.mail,
      name: element.name,
      surname: element.surname,
      userActive: element.userActive,
      phoneNumber: element.phoneNumber,
      id: element.id,
      modify: "toRemove"
    }));

    members.otherUsers.forEach(element => membersToShow.push({
      mail: element.mail,
      name: element.name,
      surname: element.surname,
      userActive: element.userActive,
      phoneNumber: element.phoneNumber,
      id: element.id,
      modify: "toAdd"
    }));

    this.membersToShow = new MatTableDataSource<MemberToModify>(membersToShow);

    this.preparePaginator();
  }

  private loadData(): void
  {
    this.http.get<GroupData>(ApplicationSettings.apiUrl + "/api/v1/group/" + this.groupId).subscribe({
      next: groupDetails => {
        this.groupDetails = groupDetails;

        this.http.get<GroupMembers>(ApplicationSettings.apiUrl + "/api/v1/group/members/management/" + this.groupId).subscribe({
          next: result => {
            this.filter = "";
            this.prepareTable(result);
          },
          error: err => this.showLoadingError(err)
        })
      },
      error: err => this.showLoadingError(err)
    })
  }

  addMember(userId: string): void
  {
    this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/group/member/add/" + this.groupId, {userId: userId}).subscribe({
      next: result =>  {
        this.notifier.notify(result.code.toLowerCase(), result.message);
        this.loadData();
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
      }
    });
  }

  removeMember(userId: string): void
  {
    this.http.delete<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/group/member/remove/" + this.groupId, {body: {userId: userId}}).subscribe({
      next: result =>  {
        this.notifier.notify(result.code.toLowerCase(), result.message);
        this.loadData();
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
      }
    });
  }

  ngOnInit(): void
  {
    this.groupId = this.data;
    this.loadData();
  }

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.membersToShow.filter = filterValue.trim().toLowerCase();

    if(this.membersToShow.paginator)
      this.membersToShow.paginator.firstPage();
  }
}

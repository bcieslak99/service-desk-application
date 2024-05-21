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
import {NewGroupDialogComponent} from "../dialogs/new-group-dialog/new-group-dialog.component";
import {ServerResponsesMessage} from "../../../../models/server-responses.interfaces";
import {EditGroupDataDialogComponent} from "../dialogs/edit-group-data-dialog/edit-group-data-dialog.component";
import {
  GroupMembersManagementDialogComponent
} from "../dialogs/group-members-management-dialog/group-members-management-dialog.component";
import {GroupManagerDialogComponent} from "../dialogs/group-manager-dialog/group-manager-dialog.component";

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

  activateGroup(groupId: string): void
  {
    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/group/activate/" + groupId, {}).subscribe({
      next: value => {
        this.loadGroups();
        this.notifier.notify(value.code.toLowerCase(), value.message);
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify(result.code.toLowerCase(), result.message);
      }
    });
  }

  deactivateGroup(groupId: string): void
  {
    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/group/deactivate/" + groupId, {}).subscribe({
      next: value => {
        this.loadGroups();
        this.notifier.notify(value.code.toLowerCase(), value.message);
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify(result.code.toLowerCase(), result.message);
      }
    });
  }

  openDialogToCreateNewGroup()
  {
    const newGroupDialog = this.dialog.open(NewGroupDialogComponent);

    newGroupDialog.afterClosed().subscribe(result => {
      if(result) this.loadGroups();
    })
  }

  openDialogToEditGroupData(groupData: GroupData): void
  {
    const editGroupDataDialog = this.dialog.open(EditGroupDataDialogComponent, {
      data: groupData
    });

    editGroupDataDialog.afterClosed().subscribe(result => {
      if(result) this.loadGroups();
    });
  }

  openDialogToManagementGroupMembers(groupId: string): void
  {
    const managementMembersDialog = this.dialog.open(GroupMembersManagementDialogComponent, {
      data: groupId
    });
  }

  openDialogToSetManager(groupId: string)
  {
    const managerDialog = this.dialog.open(GroupManagerDialogComponent, {
      data: groupId
    })
  }
}

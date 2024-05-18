import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserAsListElement} from "../../../../models/user-data.interfaces";
import {ApplicationSettings} from "../../../../application-settings";
import {NotifierService} from "angular-notifier";
import {Observable} from "rxjs";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatDialog} from "@angular/material/dialog";
import {NewUserDialogComponent} from "../dialogs/new-user-dialog/new-user-dialog.component";
import {ChangePasswordDialogComponent} from "../dialogs/change-password-dialog/change-password-dialog.component";
import {EditUserDataDialogComponent} from "../dialogs/edit-user-data-dialog/edit-user-data-dialog.component";

@Component({
  selector: 'app-users-management',
  templateUrl: './users-management.component.html',
  styleUrls: ['./users-management.component.css']
})
export class UsersManagementComponent implements AfterViewInit
{
  filter: string = "";
  displayedColumns: string[] = ["surname", "name", "mail", "userActive", "activities"];
  listOfUsers: MatTableDataSource<UserAsListElement> = new MatTableDataSource<UserAsListElement>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private http: HttpClient, private notifier: NotifierService, private dialog: MatDialog) {}

  private getUsersList(): Observable<UserAsListElement[]>
  {
    return this.http.get<UserAsListElement[]>(ApplicationSettings.apiUrl + "/api/v1/user/list/all");
  }

  preparePaginator(): void
  {
    this.listOfUsers.paginator = this.paginator;
    this.listOfUsers.sort = this.sort;
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

  private loadUsers(): void
  {
    this.getUsersList().subscribe({
      next: value =>  {
        this.listOfUsers = new MatTableDataSource<UserAsListElement>(value);
        this.filter = "";
        this.preparePaginator();
      },
      error: err => {
        this.notifier.notify("error", "Błąd komunikacji z serwerem! Nie udało się pobrać listy użytkowników!")
      }
    })
  }

  ngAfterViewInit(): void
  {
    this.loadUsers();
  }

  applyFilter(event: Event)
  {
    const filterValue = (event.target as HTMLInputElement).value;
    this.listOfUsers.filter = filterValue.trim().toLowerCase();

    if (this.listOfUsers.paginator)
      this.listOfUsers.paginator.firstPage();
  }

  openDialogForNewUser()
  {
    const newUserDialog = this.dialog.open(NewUserDialogComponent);

    newUserDialog.afterClosed().subscribe(result => {
      if(result) this.loadUsers();
    })
  }

  openDialogForChangePassword(userData: UserAsListElement)
  {
    const dialogForChangePassword = this.dialog.open(ChangePasswordDialogComponent, {
      data: {
        userData: userData,
      }
    })
  }

  openDialogForEditUserData(userId: string)
  {
    const dialogForEditUserData = this.dialog.open(EditUserDataDialogComponent, {
      data: {
        userId: userId
      }
    });

    dialogForEditUserData.afterClosed().subscribe(result => {
      if(result) this.loadUsers();
    });
  }
}

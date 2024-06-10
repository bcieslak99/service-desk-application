import {AfterViewInit, Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {UserAsListElement} from "../../../../../models/user-data.interfaces";

@Component({
  selector: 'app-selecting-user-dialog',
  templateUrl: './selecting-user-dialog.component.html',
  styleUrls: ['./selecting-user-dialog.component.css']
})
export class SelectingUserDialogComponent implements AfterViewInit
{
  userTableSource: MatTableDataSource<UserAsListElement>
  users: UserAsListElement[];
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  columns: string[] = ["name", "surname", "mail", "action"];

  constructor(public dialog: MatDialogRef<SelectingUserDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private notifier: NotifierService, private http: HttpClient)
  {
    this.users = data;
    this.userTableSource = new MatTableDataSource<UserAsListElement>(data);
  }

  preparePaginator(): void
  {
    this.userTableSource.paginator = this.paginator;
    this.userTableSource.sort = this.sort;
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
    this.userTableSource.filter = filterValue.trim().toLowerCase();

    if(this.userTableSource.paginator)
      this.userTableSource.paginator.firstPage();
  }

  closeDialog(user: UserAsListElement | null): void
  {
    this.dialog.close(user);
  }

  selectUser(userId: string): void
  {
    const filteredUsers: UserAsListElement[] = this.users.filter(user => user.userId.trim() === userId.trim());
    const user: UserAsListElement | null = filteredUsers.length > 0 ? filteredUsers[0] : null;
    this.closeDialog(user);
  }

  ngAfterViewInit(): void
  {
    this.preparePaginator();
  }
}

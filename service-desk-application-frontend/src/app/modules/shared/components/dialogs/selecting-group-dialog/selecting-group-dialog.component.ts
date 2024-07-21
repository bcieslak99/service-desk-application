import {AfterViewInit, Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MatTableDataSource} from "@angular/material/table";
import {GroupData} from "../../../../../models/group-data.interfaces";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-selecting-group-dialog',
  templateUrl: './selecting-group-dialog.component.html',
  styleUrls: ['./selecting-group-dialog.component.css']
})
export class SelectingGroupDialogComponent implements AfterViewInit
{
  groupsTableSource: MatTableDataSource<GroupData>;
  groups: GroupData[];
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  columns: string[] = ["name", "description", "action"];

  constructor(public dialog: MatDialogRef<SelectingGroupDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any)
  {
    this.groups = data;
    this.groupsTableSource = new MatTableDataSource<GroupData>(data);
  }

  preparePaginator(): void
  {
    this.groupsTableSource.paginator = this.paginator;
    this.groupsTableSource.sort = this.sort;
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
    this.groupsTableSource.filter = filterValue.trim().toLowerCase();

    if(this.groupsTableSource.paginator)
      this.groupsTableSource.paginator.firstPage();
  }

  closeDialog(user: GroupData | null): void
  {
    this.dialog.close(user);
  }

  selectGroup(user: GroupData): void
  {
    this.closeDialog(user);
  }

  ngAfterViewInit(): void
  {
    this.preparePaginator();
  }
}

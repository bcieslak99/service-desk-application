import {AfterViewInit, Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {MatTableDataSource} from "@angular/material/table";
import {TicketCategory} from "../../../../../models/ticket.interfaces";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-selecting-category-dialog',
  templateUrl: './selecting-category-dialog.component.html',
  styleUrls: ['./selecting-category-dialog.component.css']
})
export class SelectingCategoryDialogComponent implements AfterViewInit
{
  categoriesTableSource: MatTableDataSource<TicketCategory>
  categories: TicketCategory[];
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  columns: string[] = ["name", "description", "action"];

  preparePaginator(): void
  {
    this.categoriesTableSource.paginator = this.paginator;
    this.categoriesTableSource.sort = this.sort;
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

  constructor(public dialog: MatDialogRef<SelectingCategoryDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
             private notifier: NotifierService, private http: HttpClient)
  {
    this.categories = data;
    this.categoriesTableSource = new MatTableDataSource<TicketCategory>(data);
  }

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.categoriesTableSource.filter = filterValue.trim().toLowerCase();

    if(this.categoriesTableSource.paginator)
      this.categoriesTableSource.paginator.firstPage();
  }

  closeDialog(category: TicketCategory | null): void
  {
    this.dialog.close(category);
  }

  selectCategory(categoryId: string): void
  {
    const filteredCategories: TicketCategory[] = this.categories
      .filter(category => category.categoryId.trim() === categoryId.trim());

    const category: TicketCategory | null = filteredCategories.length > 0 ? filteredCategories[0] : null;

    this.closeDialog(category);
  }

  ngAfterViewInit(): void
  {
    this.preparePaginator();
  }
}

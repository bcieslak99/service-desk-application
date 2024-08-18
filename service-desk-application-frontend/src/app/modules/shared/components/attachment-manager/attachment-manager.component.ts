import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NotifierService} from "angular-notifier";
import {ServerResponsesMessage} from "../../../../models/server-responses.interfaces";
import {map} from "rxjs";
import {MatTableDataSource} from "@angular/material/table";
import {Attachment} from "../../../../models/ticket.interfaces";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";

@Component({
  selector: 'app-attachment-manager',
  templateUrl: './attachment-manager.component.html',
  styleUrls: ['./attachment-manager.component.css']
})
export class AttachmentManagerComponent implements AfterViewInit
{
  @Input() ticketId: string | null = null;
  selectedFile?: File;
  dataSource: MatTableDataSource<Attachment> = new MatTableDataSource<Attachment>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  columns: string[] = ["name", "action"];

  constructor(private http: HttpClient, private notifier: NotifierService) {}

  uploadFile(file: File)
  {
    const formData: FormData = new FormData();
    formData.append("file", file);
    return this.http.post<ServerResponsesMessage>("http://localhost:8080/api/v1/ticket/attachment/add/" + this.ticketId, formData);
  }

  downloadFile(fileId: string)
  {
    return this.http.get("http://localhost:8080/api/v1/ticket/attachment/download/" + fileId, { responseType: 'blob' }).pipe(
      map((res: Blob) => {
        return res;
      })
    );
  }

  onFileSelected(event: any): void
  {
    this.selectedFile = event.target.files[0];
  }

  onUpload(): void
  {
    if(this.selectedFile)
    {
      this.uploadFile(this.selectedFile).subscribe({
        next: result => {
          this.loadAttachmentsList();
          this.notifier.notify("success", "Plik został przesłany");
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się przesłąć pliku! Spróbuj jeszcze raz.");
        }
      });
    }
  }

  onDownload(fileId: string, filename: string): void
  {
    this.downloadFile(fileId).subscribe({
      next: result => {
        const blob = new Blob([result]);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać pliku!");
      }
    });
  }

  loadAttachmentsList(): void
  {
    this.http.get<Attachment[]>("http://localhost:8080/api/v1/ticket/attachment/list/" + this.ticketId).subscribe({
      next: result => {
        this.dataSource = new MatTableDataSource<Attachment>(result);
        this.preparePaginator();
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać listy załączników!");
      }
    });
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

  applyFilter(event: Event): void
  {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if(this.dataSource.paginator)
      this.dataSource.paginator.firstPage();
  }

  ngAfterViewInit(): void
  {
    this.loadAttachmentsList();
  }
}

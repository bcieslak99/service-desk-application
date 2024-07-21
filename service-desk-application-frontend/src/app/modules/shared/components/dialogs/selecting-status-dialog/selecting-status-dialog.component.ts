import {Component, Inject} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NewTicketStatus, StatusAsListElement, TicketStatus} from "../../../../../models/ticket.interfaces";

@Component({
  selector: 'app-selecting-status-dialog',
  templateUrl: './selecting-status-dialog.component.html',
  styleUrls: ['./selecting-status-dialog.component.css']
})
export class SelectingStatusDialogComponent
{
  statusTableSource: MatTableDataSource<StatusAsListElement>;
  columns: string[] = ["status", "action"];
  comment: string = "";

  constructor(public dialog: MatDialogRef<SelectingStatusDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any)
  {
    const statuses: StatusAsListElement[] = [
      {
        ticketStatus: TicketStatus.PENDING,
        polishVersion: "Oczekujące"
      },
      {
        ticketStatus: TicketStatus.IN_PROGRESS,
        polishVersion: "W trakcie"
      },
      {
        ticketStatus: TicketStatus.ON_HOLD,
        polishVersion: "Wstrzymane"
      },
      {
        ticketStatus: TicketStatus.RESOLVED,
        polishVersion: "Rozwiązane"
      },
    ];

    this.statusTableSource = new MatTableDataSource<StatusAsListElement>(statuses);
  }

  closeDialog(ticketStatus: NewTicketStatus | null): void
  {
    this.dialog.close(ticketStatus);
  }

  selectStatus(ticketStatus: TicketStatus): void
  {
    this.closeDialog({newTicketStatus: ticketStatus, comment: this.comment});
  }
}

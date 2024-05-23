import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TicketCategory} from "../../../../../models/ticket.interfaces";
import {ApplicationSettings} from "../../../../../application-settings";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";
import {GroupData} from "../../../../../models/group-data.interfaces";

@Component({
  selector: 'app-edit-ticket-category-data-dialog',
  templateUrl: './edit-ticket-category-data-dialog.component.html',
  styleUrls: ['./edit-ticket-category-data-dialog.component.css']
})
export class EditTicketCategoryDataDialogComponent implements OnInit
{
  categoryId: string = "";
  groups: GroupData[] = [];
  categoryForm: FormGroup = new FormGroup({
    name: new FormControl("", [Validators.minLength(3), Validators.maxLength(300), Validators.required]),
    description: new FormControl("", [Validators.minLength(5), Validators.maxLength(1000), Validators.required]),
    ticketType: new FormControl(""),
    defaultGroup: new FormControl(""),
  });

  constructor(public dialog: MatDialogRef<EditTicketCategoryDataDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private notifier: NotifierService, private http: HttpClient) {}

  getErrorNameMessage(): string | null
  {
    const nameControl = this.categoryForm.controls["name"];

    if(nameControl.invalid)
    {
      if(nameControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(nameControl.hasError("minlength"))
        return "Nazwa kategorii musi się składać z minimum 3 znkaów!";
      else if(nameControl.hasError("maxlength"))
        return "Nazwa kategorii może się składać maksymalnie z 300 znaków!";
      else return "Nieznany błąd."
    }
    else return null;
  }

  getErrorDescriptionMessage(): string | null
  {
    const descriptionControl = this.categoryForm.controls["description"];

    if(descriptionControl.invalid)
    {
      if(descriptionControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(descriptionControl.hasError("minlength"))
        return "Opis kategorii musi się składać z minimum 5 znkaów!";
      else if(descriptionControl.hasError("maxlength"))
        return "Opis kategorii może się składać maksymalnie z 1000 znaków!";
      else return "Nieznany błąd."
    }
    else return null;
  }

  closeDialog(result: boolean): void
  {
    this.dialog.close(result);
  }

  loadData(): void
  {
    this.http.get<GroupData[]>(ApplicationSettings.apiUrl + "/api/v1/group/list").subscribe({
      next: result => {
        this.groups = result;
        this.loadCategoryData();
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się zainicjalizować formularza");
        this.dialog.close(false);
      }
    });
  }

  loadCategoryData(): void
  {
    this.http.get<TicketCategory>(ApplicationSettings.apiUrl + "/api/v1/category/details/" + this.categoryId).subscribe({
      next: data => {
        this.categoryForm.controls["name"].setValue( data.name);
        this.categoryForm.controls["description"].setValue(data.description);
        this.categoryForm.controls["ticketType"].setValue(data.ticketType);
        this.categoryForm.controls["defaultGroup"].setValue(data.defaultGroup.groupId);

        if(this.groups.filter(element => element.groupId === data.defaultGroup.groupId).length < 1)
          this.groups.push(data.defaultGroup);
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify(result.code.toLowerCase(), result.message);
        this.closeDialog(false);
      }
    })
  }

  ngOnInit(): void
  {
    this.categoryId = this.data;
    this.loadData();
  }

  dataIsCorrect(): boolean
  {
    return !this.categoryForm.invalid;
  }

  editCategory(): void
  {
    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/category/edit/" + this.categoryId, this.categoryForm.getRawValue())
      .subscribe({
        next: result => {
          this.notifier.notify(result.code.toLowerCase(), result.message);
          this.closeDialog(true);
        },
        error: err => {
          let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
          this.notifier.notify(result.code.toLowerCase(), result.message);
        }
      });
  }
}

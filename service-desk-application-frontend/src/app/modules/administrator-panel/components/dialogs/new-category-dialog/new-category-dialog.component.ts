import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {GroupData} from "../../../../../models/group-data.interfaces";
import {ApplicationSettings} from "../../../../../application-settings";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";

@Component({
  selector: 'app-new-category-dialog',
  templateUrl: './new-category-dialog.component.html',
  styleUrls: ['./new-category-dialog.component.css']
})
export class NewCategoryDialogComponent implements OnInit
{
  activeGroups: GroupData[] = [];

  categoryForm: FormGroup = new FormGroup({
    name: new FormControl("", [Validators.minLength(3), Validators.maxLength(300), Validators.required]),
    description: new FormControl("", [Validators.minLength(5), Validators.maxLength(1000), Validators.required]),
    ticketType: new FormControl(""),
    defaultGroup: new FormControl(""),
  });

  constructor(private dialogRef: MatDialogRef<NewCategoryDialogComponent>, private notifier: NotifierService, private http: HttpClient) {}

  loadGroups(): void
  {
    this.http.get<GroupData[]>(ApplicationSettings.apiUrl + "/api/v1/group/list").subscribe({
      next: result => {
        this.activeGroups = result;
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się zainicjalizować formularza");
        this.dialogRef.close(false);
      }
    });
  }

  dataIsCorrect(): boolean
  {
    return !this.categoryForm.invalid;
  }

  closeDialog(): void
  {
    this.dialogRef.close(false);
  }

  createCategory(): void
  {
    console.log(this.categoryForm.getRawValue())

    this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/category/create", this.categoryForm.getRawValue()).subscribe({
      next: result=> {
        this.notifier.notify(result.code.toLowerCase(), result.message);
        this.dialogRef.close(true);
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify(result.code.toLowerCase(), result.message);
      }
    })
  }

  ngOnInit(): void
  {
    this.loadGroups();
  }
}

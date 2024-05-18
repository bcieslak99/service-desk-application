import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {NewUserDialogComponent} from "../new-user-dialog/new-user-dialog.component";
import {NewGroupData} from "../../../../../models/group-data.interfaces";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";
import {ApplicationSettings} from "../../../../../application-settings";

@Component({
  selector: 'app-new-group-dialog',
  templateUrl: './new-group-dialog.component.html',
  styleUrls: ['./new-group-dialog.component.css']
})
export class NewGroupDialogComponent
{
  private refreshGroups: boolean = false;
  newGroupForm = new FormGroup({
    name: new FormControl("", [Validators.minLength(5), Validators.maxLength(50), Validators.required]),
    description: new FormControl("", [Validators.minLength(5), Validators.maxLength(1000), Validators.required]),
    groupType: new FormControl("FIRST_LINE", [Validators.required]),
    groupActive: new FormControl(true)
  });

  constructor(private dialogRef: MatDialogRef<NewUserDialogComponent>, private notifier: NotifierService, private http: HttpClient) {}

  getErrorNameMessage(): string | null
  {
    const nameControl = this.newGroupForm.controls["name"];

    if(nameControl.invalid)
    {
      if(nameControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(nameControl.hasError("minlength"))
        return "Nazwa grupy musi się składać minimum z 5 znkaów!";
      else if(nameControl.hasError("maxlength"))
        return "Nazwa grupy może się składać maksymalnie z 150 znaków!";
      else return "Nieznany błąd!";
    }
    else return null;
  }

  getErrorDescriptionMessage(): string | null
  {
    const descriptionControl = this.newGroupForm.controls["description"];

    if(descriptionControl.invalid)
    {
      if(descriptionControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(descriptionControl.hasError("minlength"))
        return "Opis grupy musi się składać minimum z 5 znkaów!";
      else if(descriptionControl.hasError("maxlength"))
        return "Opis grupy może się składać maksymalnie z 1000 znaków!";
      else return "Nieznany błąd!";
    }
    else return null
  }

  getErrorGroupTypeMessage(): string | null
  {
    const groupTypeControl = this.newGroupForm.controls["groupType"];

    if(groupTypeControl.invalid && groupTypeControl.hasError("required"))
      return "To pole jest wymagane!";
    else return null;
  }

  dataIsCorrect(): boolean
  {
    return !this.newGroupForm.invalid;
  }

  closeDialog(): void
  {
    this.dialogRef.close(this.refreshGroups);
  }

  createGroup(): void
  {
    const data = this.newGroupForm.getRawValue();

    let newGroup: NewGroupData = {
      name: data.name !== null ? data.name : "",
      description: data.description !== null ? data.description : "",
      groupType: data.groupType !== null ? data.groupType : "",
      groupActive: data.groupActive !== null ? data.groupActive : false,
      managerId: null
    }

    this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/group/create", newGroup).subscribe({
      next: value => {
        this.notifier.notify(value.code.toLowerCase(), value.message);
        this.refreshGroups = true;
        this.closeDialog();
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
      }
    });
  }
}

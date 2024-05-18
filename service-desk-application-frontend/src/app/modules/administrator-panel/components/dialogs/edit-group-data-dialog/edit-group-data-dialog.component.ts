import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {GroupData, NewGroupData} from "../../../../../models/group-data.interfaces";
import {ApplicationSettings} from "../../../../../application-settings";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";

@Component({
  selector: 'app-edit-group-data-dialog',
  templateUrl: './edit-group-data-dialog.component.html',
  styleUrls: ['./edit-group-data-dialog.component.css']
})
export class EditGroupDataDialogComponent implements OnInit
{
  private groupId!: string;
  groupDataForm: FormGroup = new FormGroup({
    name: new FormControl("", [Validators.minLength(5), Validators.maxLength(50), Validators.required]),
    description: new FormControl("", [Validators.minLength(5), Validators.maxLength(1000), Validators.required]),
    groupType: new FormControl("FIRST_LINE", [Validators.required]),
    groupActive: new FormControl(true)
  });

  constructor(public dialog: MatDialogRef<EditGroupDataDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private notifier: NotifierService, private http: HttpClient) {}

  closeDialog(dataSaved: boolean): void
  {
    this.dialog.close(dataSaved);
  }

  loadGroupDetails(): void
  {
    this.http.get<GroupData>(ApplicationSettings.apiUrl + "/api/v1/group/" + this.groupId).subscribe({
      next: value => {
        this.groupDataForm.controls["name"].setValue(value.name);
        this.groupDataForm.controls["description"].setValue(value.description);
        this.groupDataForm.controls["groupType"].setValue(value.groupType);
        this.groupDataForm.controls["groupActive"].setValue(value.groupActive);
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać danych użytkownika!");
        this.closeDialog(false);
      }
    });
  }

  dataIsCorrect(): boolean
  {
    return !this.groupDataForm.invalid;
  }

  getErrorNameMessage(): string | null
  {
    const nameControl = this.groupDataForm.controls["name"];

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
    const descriptionControl = this.groupDataForm.controls["description"];

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
    const groupTypeControl = this.groupDataForm.controls["groupType"];

    if(groupTypeControl.invalid && groupTypeControl.hasError("required"))
      return "To pole jest wymagane!";
    else return null;
  }

  editData(): void
  {
    const data = this.groupDataForm.getRawValue();

    let newData: NewGroupData = {
      name: data.name !== null ? data.name : "",
      description: data.description !== null ? data.description : "",
      groupType: data.groupType !== null ? data.groupType : "",
      groupActive: data.groupActive !== null ? data.groupActive : false,
      managerId: null
    }

    this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/group/edit/" + this.groupId, newData).subscribe({
      next: value => {
        this.notifier.notify(value.code.toLowerCase(), value.message);
        this.closeDialog(true);
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
        console.log(err.message)
      }
    });
  }

  getGroupName(): string
  {
    return this.data.name;
  }

  ngOnInit(): void
  {
    this.groupId = this.data.groupId;
    this.loadGroupDetails();
  }
}

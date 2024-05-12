import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {NewUser, NewUserData} from "../../../../../models/user-data.interfaces";
import {ApplicationSettings} from "../../../../../application-settings";
import {Observable} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";

@Component({
  selector: 'app-edit-user-data-dialog',
  templateUrl: './edit-user-data-dialog.component.html',
  styleUrls: ['./edit-user-data-dialog.component.css']
})
export class EditUserDataDialogComponent implements OnInit
{
  private userId!: string;
  private userDetails!: NewUser;
  userDataForm: FormGroup = new FormGroup({
    name: new FormControl("", [Validators.minLength(3), Validators.maxLength(150), Validators.required]),
    surname: new FormControl("", [Validators.minLength(2), Validators.maxLength(150), Validators.required]),
    mail: new FormControl("", [Validators.email, Validators.minLength(5), Validators.maxLength(325), Validators.required]),
    phoneNumber: new FormControl("", [Validators.maxLength(14)]),
    userActive: new FormControl(true),
    administrator: new FormControl(false),
    accessAsEmployee: new FormControl(true)
  });

  constructor(public dialog: MatDialogRef<EditUserDataDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private notifier: NotifierService, private http: HttpClient) {}

  private getUserDetails(): Observable<NewUser>
  {
    return this.http.get<NewUser>(ApplicationSettings.apiUrl + "/api/v1/user/details/" + this.userId);
  }

  closeDialog(dataSaved: boolean): void
  {
    this.dialog.close(dataSaved);
  }

  getErrorSurnameMessage(): string | null
  {
    const surnameControl = this.userDataForm.controls["surname"];

    if(surnameControl.invalid)
    {
      if(surnameControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(surnameControl.hasError("minlength"))
        return "Nazwisko musi się składać z minimum 2 znaków!";
      else if(surnameControl.hasError("maxlength"))
        return "Nazwisko musi się składać maksymalnie z 150 znaków!";
      else return "Nieznany błąd!";
    }

    return null;
  }

  getErrorNameMessage(): string | null
  {
    const nameControl = this.userDataForm.controls["name"];

    if(nameControl.invalid)
    {
      if(nameControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(nameControl.hasError("minlength"))
        return "Imię musi się skłądać z minimum 3 znaków!";
      else if(nameControl.hasError("maxlength"))
        return "Imię może się składać maksymalnie z 150 znaków!";
      else return "Nieznany błąd!";
    }

    return null;
  }

  getErrorMailMessage(): string | null
  {
    const mailControl = this.userDataForm.controls["mail"];

    if(mailControl.invalid)
    {
      if(mailControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(mailControl.hasError("minlength"))
        return "E-mail musi się składać z minimum 5 znaków!";
      else if(mailControl.hasError("maxlength"))
        return "E-mail może się składać z maksymalnie 325 znaków!";
      else if(mailControl.hasError("email"))
        return "W tym polu musi się znaleźć adres e-mail!";
      else return "Nieznany błąd!";
    }

    return null;
  }

  getErrorPhoneNumberMessage(): string | null
  {
    const phoneNumberControl = this.userDataForm.controls["phoneNumber"];

    if(phoneNumberControl.invalid)
    {
      if(phoneNumberControl.hasError("minlength"))
        return "Numer telefonu może składać się maksymalnie z 14 znaków!";
      else return "Nieznany błąd!";
    }

    return null;
  }

  dataIsCorrect(): boolean
  {
    return !this.userDataForm.invalid;
  }

  private loadUserDetails()
  {
    this.getUserDetails().subscribe({
      next: value => {
        this.userDetails = value;

        this.userDataForm.controls["surname"].setValue(this.userDetails.surname);
        this.userDataForm.controls["name"].setValue(this.userDetails.name);
        this.userDataForm.controls["mail"].setValue(this.userDetails.mail);
        this.userDataForm.controls["phoneNumber"].setValue(this.userDetails.phoneNumber !== null ? this.userDetails.phoneNumber : "");
        this.userDataForm.controls["userActive"].setValue(this.userDetails.active);
        this.userDataForm.controls["administrator"].setValue(this.userDetails.administrator);
        this.userDataForm.controls["accessAsEmployee"].setValue(this.userDetails.accessAsEmployeeIsPermitted);
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać danych użytkownika!");
        this.closeDialog(false);
      }
    })
  }

  private prepareNewUserData(): NewUserData
  {
    const data = this.userDataForm.getRawValue();

    return {
      surname: data.surname,
      name: data.name,
      mail: data.mail,
      phoneNumber: data.phoneNumber,
      administrator: data.administrator,
      accessAsEmployeeIsPermitted: data.accessAsEmployee,
      active: data.userActive
    }
  }

  sendNewUserData(): Observable<ServerResponsesMessage>
  {
    return this.http.patch<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/user/edit/" + this.userId, this.prepareNewUserData());
  }

  editData()
  {
    this.sendNewUserData().subscribe({
      next: value => {
        this.notifier.notify("success", "Dane zostały zapisane.");
        this.closeDialog(true);
      },
      error: err => {
        console.log(err.error.message)
        this.notifier.notify("error", "Nie udało się edytować danych użytkownika!");
      }
    })
  }

  ngOnInit(): void
  {
    this.userId = this.data.userId;
    this.loadUserDetails();
  }
}

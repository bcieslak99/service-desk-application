import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {NotifierService} from "angular-notifier";
import {NewUser} from "../../../../../models/user-data.interfaces";
import {HttpClient} from "@angular/common/http";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";
import {ApplicationSettings} from "../../../../../application-settings";
import {Observable} from "rxjs";

@Component({
  selector: 'app-new-user-dialog',
  templateUrl: './new-user-dialog.component.html',
  styleUrls: ['./new-user-dialog.component.css']
})
export class NewUserDialogComponent
{
  refreshUserList: boolean = false;
  registerForm: FormGroup = new FormGroup({
    name: new FormControl("", [Validators.minLength(3), Validators.maxLength(150), Validators.required]),
    surname: new FormControl("", [Validators.minLength(2), Validators.maxLength(150), Validators.required]),
    mail: new FormControl("", [Validators.email, Validators.minLength(5), Validators.maxLength(325), Validators.required]),
    password: new FormControl("", [Validators.minLength(8), Validators.maxLength(120), Validators.required]),
    confirmPassword: new FormControl("", [Validators.minLength(8), Validators.maxLength(120), Validators.required]),
    phoneNumber: new FormControl("", [Validators.maxLength(14)]),
    userActive: new FormControl(true),
    administrator: new FormControl(false),
    accessAsEmployee: new FormControl(true)
  });

  constructor(private dialogRef: MatDialogRef<NewUserDialogComponent>, private notifier: NotifierService, private http: HttpClient) {}

  getErrorSurnameMessage(): string | null
  {
    const surnameControl = this.registerForm.controls["surname"];

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
    const nameControl = this.registerForm.controls["name"];

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
    const mailControl = this.registerForm.controls["mail"];

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

  getErrorPasswordMessage(): string | null
  {
    const passwordControl = this.registerForm.controls["password"];

    if(passwordControl.invalid)
    {
      if(passwordControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(passwordControl.hasError("minlength"))
        return "Hasło musi się składać z minimum 8 znaków!";
      else if(passwordControl.hasError("maxlength"))
        return "Hasło może się składać maksymalnie z 120 znaków!";
      else return "Nieznany błąd!";
    }

    return null;
  }

  getErrorPasswordConfirmation(): string | null
  {
    const passwordControl = this.registerForm.controls["confirmPassword"];

    if(passwordControl.invalid)
    {
      if(passwordControl.hasError("required"))
        return "To pole jest wymagane!";
      else if(passwordControl.hasError("minlength"))
        return "Hasło musi się składać z minimum 8 znaków!";
      else if(passwordControl.hasError("maxlength"))
        return "Hasło może się składać maksymalnie z 120 znaków!";
      else return "Nieznany błąd!";
    }

    return null;
  }

  getErrorPhoneNumberMessage(): string | null
  {
    const phoneNumberControl = this.registerForm.controls["phoneNumber"];

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
    return !this.registerForm.invalid;
  }

  private getNewUserObject(): NewUser
  {
    const userDataFromForm = this.registerForm.getRawValue();

    return {
      surname: userDataFromForm.surname,
      name: userDataFromForm.name,
      mail: userDataFromForm.mail,
      password: userDataFromForm.password,
      phoneNumber: userDataFromForm.phoneNumber,
      active: userDataFromForm.userActive,
      administrator: userDataFromForm.administrator,
      accessAsEmployeeIsPermitted: userDataFromForm.accessAsEmployee
    };
  }

  private sendNewUserData(userData: NewUser): Observable<ServerResponsesMessage>
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/auth/register", userData);
  }

  registerUser(): void
  {
    if(this.registerForm.invalid)
    {
      this.notifier.notify("error", "Formularz zawiera błędy!");
      return;
    }

    if(this.registerForm.getRawValue().password !== this.registerForm.getRawValue().confirmPassword)
    {
      this.notifier.notify("error", "Hasło różni się od tego do potwierdzenia!");
      return;
    }

    this.sendNewUserData(this.getNewUserObject()).subscribe({
      next: result => {
        this.notifier.notify(result.code.toLowerCase(), result.message);
        this.refreshUserList = true;
        this.closeDialog();
      },
      error: err => {
        const response : ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify(response.code.toLowerCase(), response.message);
      }
    })
  }

  closeDialog(): void
  {
    this.dialogRef.close(this.refreshUserList);
  }
}

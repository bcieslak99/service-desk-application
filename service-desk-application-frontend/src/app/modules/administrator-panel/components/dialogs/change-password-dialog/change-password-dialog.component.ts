import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NewPassword, UserAsListElement} from "../../../../../models/user-data.interfaces";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {NotifierService} from "angular-notifier";
import {HttpClient} from "@angular/common/http";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";
import {ApplicationSettings} from "../../../../../application-settings";

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.css']
})
export class ChangePasswordDialogComponent implements OnInit
{
  userData!: UserAsListElement;

  changePasswordForm: FormGroup = new FormGroup({
    password: new FormControl("", [Validators.minLength(8), Validators.maxLength(120), Validators.required]),
    confirmPassword: new FormControl("", [Validators.minLength(8), Validators.maxLength(120), Validators.required])
  });

  constructor(public dialog: MatDialogRef<ChangePasswordDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private notifier: NotifierService, private http: HttpClient) {}

  ngOnInit(): void
  {
    this.userData = this.data.userData;
  }

  showUserDetail(): string
  {
    return `${this.userData.surname} ${this.userData.name} (${this.userData.mail})`;
  }

  getErrorPasswordMessage(): string | null
  {
    const passwordControl = this.changePasswordForm.controls["password"];

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
    const passwordControl = this.changePasswordForm.controls["confirmPassword"];

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

  dataIsCorrect(): boolean
  {
    if(this.changePasswordForm.invalid) return false;

    const data = this.changePasswordForm.getRawValue();

    if(data.password !== data.confirmPassword)
    {
      this.notifier.notify("error", "Hasło różni się od tego do potwierdzenia!");
      return false;
    }

    return true;
  }

  closeDialog(): void
  {
    this.dialog.close();
  }

  private sendNewPassword(password: NewPassword)
  {
    return this.http.post<ServerResponsesMessage>(ApplicationSettings.apiUrl + "/api/v1/auth/password/change/" + this.userData.userId, password);
  }

  changePassword(): void
  {
    if(!this.dataIsCorrect()) return;

    let newPassword: NewPassword = {
      password: this.changePasswordForm.getRawValue().password
    }

    this.sendNewPassword(newPassword).subscribe({
      next: value => {
        this.notifier.notify(value.code.toLowerCase(), value.message);
        this.closeDialog();
      },
      error: err => {
        let result = err.message as ServerResponsesMessage;
        this.notifier.notify(result.code.toLowerCase(), result.message);
      }
    })
  }
}

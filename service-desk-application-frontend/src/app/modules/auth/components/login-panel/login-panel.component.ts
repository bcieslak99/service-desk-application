import { Component } from '@angular/core';
import {AuthService} from "../../../../services/auth.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-login-panel',
  templateUrl: './login-panel.component.html',
  styleUrls: ['./login-panel.component.css']
})
export class LoginPanelComponent
{
  constructor(private auth: AuthService) {}

  loginForm: FormGroup = new FormGroup({
    mail: new FormControl("root@appliaction.local", [Validators.email, Validators.minLength(3), Validators.required]),
    password: new FormControl("b1f9def3-8a0b-4679-88a3-8eaf6af7a8b0", [Validators.minLength(3), Validators.required])
  })

  onLogin()
  {
    this.auth.loginUser(this.loginForm.getRawValue());
  }

  userDataIsChecking(): boolean
  {
    return this.auth.userDataIsChecking();
  }
}

import { Component } from '@angular/core';
import {AuthService} from "../../../../services/auth.service";

@Component({
  selector: 'app-login-panel',
  templateUrl: './login-panel.component.html',
  styleUrls: ['./login-panel.component.css']
})
export class LoginPanelComponent
{
  constructor(private auth: AuthService) {}
}

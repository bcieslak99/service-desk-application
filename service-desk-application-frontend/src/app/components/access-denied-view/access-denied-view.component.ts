import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-access-denied-view',
  templateUrl: './access-denied-view.component.html',
  styleUrls: ['./access-denied-view.component.css']
})
export class AccessDeniedViewComponent
{
  constructor(private authService: AuthService) {}

  logout(): void
  {
    this.authService.logoutUser();
  }
}

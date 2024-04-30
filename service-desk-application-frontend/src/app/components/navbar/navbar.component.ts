import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent
{
  constructor(private auth: AuthService) {}

  userIsLogged(): boolean
  {
    return this.auth.userIsLogged();
  }

  logout(): void
  {
    this.auth.logoutUser();
  }
}

import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent
{
  constructor(private auth: AuthService) {}

  userIsLogged(): BehaviorSubject<boolean>
  {
    return this.auth.userIsLogged();
  }

  logout(): void
  {
    this.auth.logoutUser();
  }
}

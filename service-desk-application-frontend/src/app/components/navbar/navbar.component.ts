import {Component} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {BehaviorSubject} from "rxjs";
import {Router} from "@angular/router";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent
{
  constructor(private auth: AuthService, private router: Router) {}

  userIsLogged(): BehaviorSubject<boolean>
  {
    return this.auth.userIsLogged();
  }

  logout(): void
  {
    this.auth.logoutUser();
  }

  getNumberOfRoles(): number
  {
    let counter: number = 0;
    const roles: string[] = this.auth.getUserRoles();

    if(roles.filter(role => role === "EMPLOYEE").length > 0) counter++;
    if(roles.filter(role => role === "SYSTEM_ADMINISTRATOR").length > 0) counter++;
    if(roles.filter(role => role === "FIRST_LINE_ANALYST" || role === "SECOND_LINE_ANALYST").length > 0) counter++;

    return counter;
  }

  redirectToAdministratorPanel(): void
  {
    this.router.navigate(["/administrator/panel"]);
  }

  redirectToAnalystPanel(): void
  {
    this.router.navigate(["/analyst/panel"]);
  }

  redirectToEmployeePanel(): void
  {
    this.router.navigate(["/employee/panel"]);
  }
}

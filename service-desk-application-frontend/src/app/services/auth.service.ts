import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApplicationSettings} from "../application-settings";
import {BehaviorSubject, Observable} from "rxjs";
import {AuthData, JWTToken, UserCredentials} from "../models/user-data.interfaces";
import {NotifierService} from "angular-notifier";
import {Router, UrlTree} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService
{
  userLogged: AuthData | null = null;
  showLogoutButton: BehaviorSubject<boolean> =  new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient, private notifier: NotifierService, private router: Router) {}

  userIsLogged(): BehaviorSubject<boolean>
  {
    return this.showLogoutButton;
  }

  private getToken(userData: UserCredentials): Observable<AuthData>
  {
    return this.http.post<AuthData>(ApplicationSettings.apiUrl + "/api/v1/auth/login", userData);
  }

  redirectUser(): UrlTree
  {
    if(this.userLogged === null) return this.router.createUrlTree(['/auth/login'])

    if(this.userLogged.roles.filter(element => element === "SYSTEM_ADMINISTRATOR").length > 0)
      return this.router.createUrlTree(["/administrator/panel"])
    else if(this.userLogged.roles.filter(element => element === "FIRST_LINE_ANALYST" || element === "SECOND_LINE_ANALYST").length > 0)
      return this.router.createUrlTree(["/employee/panel"]);
    else if(this.userLogged.roles.filter(element => element === "EMPLOYEE").length > 0)
      return this.router.createUrlTree(["/employee/panel"]);

    return this.router.createUrlTree(['/auth/login']);
  }

  loginUser(userData: UserCredentials,)
  {
    this.getToken(userData).subscribe({
      next: value => {
        this.userLogged = value;
        localStorage.setItem("userLogged", JSON.stringify(value));
        this.router.navigateByUrl(this.redirectUser());
        this.showLogoutButton.next(true);
      },
      error: err => {
        if(err.error.message !== null && err.error.message instanceof String && err.error.message.trim().length > 0) this.notifier.notify("error", err.error.message);
        else this.notifier.notify("error", "Błąd w komunikacji z serwerem.");
        console.log("test")
      }
    })
  }

  logoutUser()
  {
    this.userLogged = null;
    localStorage.removeItem("userLogged");
    this.router.navigateByUrl(this.redirectUser());
    this.showLogoutButton.next(false);
  }

  private refreshToken(authData: JWTToken): Observable<AuthData>
  {
    return this.http.post<AuthData>(ApplicationSettings.apiUrl + "/api/v1/auth/token/refresh", authData);
  }

  autoLogin(): void
  {
    const data: string | null = localStorage.getItem("userLogged");
    this.userLogged = data ? JSON.parse(data) as AuthData : null;

    if(this.userLogged !== null)
    {
      let refreshToken: JWTToken = {
        token: this.userLogged.refreshToken.token
      };

      this.refreshToken(refreshToken).subscribe({
        next: value => {
          this.userLogged = value;
          localStorage.setItem("userLogged", JSON.stringify(value));
          this.showLogoutButton.next(true);
        },
        error: err => {
          localStorage.removeItem("userLogged");
          this.userLogged = null;
          this.router.navigate(["/"]);
        }
      })
    }
  }

  getUserRoles(): string[]
  {
    if(this.userLogged === null) return []
    else return this.userLogged.roles;
  }
}

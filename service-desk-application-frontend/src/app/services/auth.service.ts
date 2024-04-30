import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApplicationSettings} from "../application-settings";
import {Observable} from "rxjs";
import {AuthData, JWTToken, UserData} from "../models/user-data.interface";
import {NotifierService} from "angular-notifier";
import {Router, UrlTree} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService
{
  userLogged: AuthData | null = null;
  checkingUserData: boolean = true;

  constructor(private http: HttpClient, private notifier: NotifierService, private router: Router) {}

  userDataIsChecking(): boolean
  {
    return this.checkingUserData;
  }

  userIsLogged(): boolean
  {
    return this.userLogged !== null;
  }

  private getToken(userData: UserData): Observable<AuthData>
  {
    return this.http.post<AuthData>(ApplicationSettings.apiUrl + "/api/v1/auth/login", userData);
  }

  redirectUser(): UrlTree
  {
    if(this.userLogged !== null)
    {
      if(this.userLogged?.roles.filter(element => element === "EMPLOYEE").length > 0)
        return this.router.createUrlTree(["/employee/panel"]);
    }

    return this.router.createUrlTree(["/auth/login"])
  }

  loginUser(userData: UserData,)
  {
    this.getToken(userData).subscribe({
      next: value => {
        this.userLogged = value;
        localStorage.setItem("userLogged", JSON.stringify(value));
        this.router.navigateByUrl(this.redirectUser())
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
    this.router.navigate(['/']);
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
          this.checkingUserData = false;
          this.router.navigateByUrl(this.redirectUser())
        },
        error: err => {
          localStorage.removeItem("userLogged");
          this.userLogged = null;
          this.checkingUserData = false;
        }
      })
    }
    else this.checkingUserData = false
  }
}

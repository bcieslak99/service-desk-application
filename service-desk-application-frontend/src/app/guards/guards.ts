import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from "@angular/router";
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {AuthData} from "../models/user-data.interface";

export const userIsNotLoggedInGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const userData: AuthData | null = inject(AuthService).userLogged;
  if(userData === null) return inject(Router).createUrlTree(["/auth/login"]);
  return true;
};

export const userIsLoggedInGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const userData: AuthData | null = inject(AuthService).userLogged;
  if(userData !== null) return inject(AuthService).redirectUser();
  return true;
}

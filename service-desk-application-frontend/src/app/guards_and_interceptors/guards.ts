import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from "@angular/router";
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {AuthData} from "../models/user-data.interface";

export const userIsLoggedInGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const userData: AuthData | null = inject(AuthService).userLogged;
  if(userData !== null) return inject(AuthService).redirectUser();
  return true;
}

export const userIsNotEmployeeGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const userData: AuthData | null = inject(AuthService).userLogged;
  if(userData === null || userData.roles.filter(element => element === "EMPLOYEE").length < 1)
    return inject(AuthService).redirectUser();

  return true;
};

export const userIsNotAdministrator: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const userData: AuthData | null = inject(AuthService).userLogged;
  if(userData === null || userData.roles.filter(element => element === "SYSTEM_ADMINISTRATOR").length < 1)
    return inject(AuthService).redirectUser();

  return true;
};

export const userIsNotAnalyst: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const userData: AuthData | null = inject(AuthService).userLogged;
  if(userData === null || userData.roles.filter(element => element === "FIRST_LINE_ANALYST" || element === "SECOND_LINE_ANALYST").length < 1)
    return inject(AuthService).redirectUser();

  return true;
};

export const userHasNotPermissions: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const userData: AuthData | null = inject(AuthService).userLogged;
  if(userData === null) return inject(Router).createUrlTree(["/auth/login"])
  else if(userData.roles.filter(element => element === "EMPLOYEE" || element === "SYSTEM_ADMINISTRATOR" ||
    element === "FIRST_LINE_ANALYST" || element === "SECOND_LINE_ANALYST").length > 0)
    return inject(AuthService).redirectUser()
  return true;
};

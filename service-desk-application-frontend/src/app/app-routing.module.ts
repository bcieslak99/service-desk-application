import { NgModule } from '@angular/core';
import {PreloadAllModules, RouterModule, Routes} from '@angular/router';
import {
  userHasNotPermissions,
  userIsLoggedInGuard,
  userIsNotAdministrator,
  userIsNotAnalyst,
  userIsNotEmployeeGuard
} from "./guards_and_interceptors/guards";
import {AccessDeniedViewComponent} from "./components/access-denied-view/access-denied-view.component";

const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "auth"
  },
  {
    path: "auth",
    loadChildren: () => import('../app/modules/auth/auth.module').then(m => m.AuthModule),
    canActivate: [userIsLoggedInGuard]
  },
  {
    path: "administrator",
    loadChildren: () => import("../app/modules/administrator-panel/administrator-panel.module").then(m => m.AdministratorPanelModule),
    canActivate: [userIsNotAdministrator]
  },
  {
    path: "analyst",
    loadChildren: () => import("../app/modules/analyst-panel/analyst-panel.module").then(m => m.AnalystPanelModule),
    canActivate: [userIsNotAnalyst]
  },
  {
    path: "employee",
    loadChildren: () => import("../app/modules/employee-panel/employee-panel.module").then(m => m.EmployeePanelModule),
    canActivate: [userIsNotEmployeeGuard]
  },
  {
    path: "access/denied",
    pathMatch: "full",
    component: AccessDeniedViewComponent,
    canActivate: [userHasNotPermissions]
  },
  {
    path: "**",
    redirectTo: "auth"
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {preloadingStrategy: PreloadAllModules})],
  exports: [RouterModule]
})
export class AppRoutingModule {}

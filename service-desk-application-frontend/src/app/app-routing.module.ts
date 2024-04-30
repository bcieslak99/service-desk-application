import { NgModule } from '@angular/core';
import {PreloadAllModules, RouterModule, Routes} from '@angular/router';
import {userIsLoggedInGuard, userIsNotLoggedInGuard} from "./guards/guards";

const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "auth"
  },
  {
    path: "auth",
    loadChildren: () => import('../app/modules/auth/auth.module').then((m) => m.AuthModule),
    canActivate: [userIsLoggedInGuard]
  },
  {
    path: "employee",
    loadChildren: () => import("../app/modules/employee-panel/employee-panel.module").then((m) => m.EmployeePanelModule),
    canActivate: [userIsNotLoggedInGuard]
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

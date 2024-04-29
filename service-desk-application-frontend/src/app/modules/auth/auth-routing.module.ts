import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {LoginPanelComponent} from "./components/login-panel/login-panel.component";

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login'
  },
  {
    path: "login",
    pathMatch: "full",
    component: LoginPanelComponent,
    title: "Service Desk :: Logowanie"
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule {}

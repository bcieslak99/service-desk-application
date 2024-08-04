import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {AnalystPanelComponent} from "./components/analyst-panel/analyst-panel.component";
import {RegisterIncidentComponent} from "./components/register/register-incident/register-incident.component";
import {
  RegisterServiceRequestComponent
} from "./components/register/register-service-request/register-service-request.component";

const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "panel"
  },
  {
    path: "panel",
    pathMatch: "full",
    component: AnalystPanelComponent,
    title: "Service Desk :: Panel Analityka"
  },
  {
    path: "ticket/incident/create",
    pathMatch: "full",
    component: RegisterIncidentComponent,
    title: "Service Desk :: Utworzenie Incydentu"
  },
  {
    path: "ticket/service/create",
    pathMatch: "full",
    component: RegisterServiceRequestComponent,
    title: "Service Desk :: Utworzenie Wniosku o Usługę"
  },
  {
    path: "**",
    redirectTo: "panel"
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AnalystPanelRoutingModule {}

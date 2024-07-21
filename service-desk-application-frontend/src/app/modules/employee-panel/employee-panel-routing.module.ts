import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {EmployeePanelComponent} from "./components/employee-panel/employee-panel.component";
import {IncidentCreatePanelComponent} from "./components/incident-create-panel/incident-create-panel.component";
import {ServiceCreatePanelComponent} from "./components/service-create-panel/service-create-panel.component";

const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "panel"
  },
  {
    path: "panel",
    pathMatch: 'full',
    component: EmployeePanelComponent,
    title: "Service Desk :: Panel Pracownika"
  },
  {
    path: "ticket/incident/create",
    pathMatch: "full",
    component: IncidentCreatePanelComponent,
    title: "Service Desk :: Utworzenie Incydentu"
  },
  {
    path: "ticket/service/create",
    pathMatch: "full",
    component: ServiceCreatePanelComponent,
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
export class EmployeePanelRoutingModule {}

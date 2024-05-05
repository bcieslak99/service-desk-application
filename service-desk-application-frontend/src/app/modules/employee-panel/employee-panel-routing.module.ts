import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {EmployeePanelComponent} from "./components/employee-panel/employee-panel.component";

const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "panel"
  },
  {
    path: "panel",
    pathMatch: 'full',
    component: EmployeePanelComponent
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

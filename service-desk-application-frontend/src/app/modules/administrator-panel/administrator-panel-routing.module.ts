import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {AdministratorPanelComponent} from "./components/administrator-panel/administrator-panel.component";

const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "panel"
  },
  {
    path: "panel",
    pathMatch: "full",
    component: AdministratorPanelComponent
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
export class AdministratorPanelRoutingModule {}

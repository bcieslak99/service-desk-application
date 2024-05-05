import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {AnalystPanelComponent} from "./components/analyst-panel/analyst-panel.component";

const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "panel"
  },
  {
    path: "panel",
    pathMatch: "full",
    component: AnalystPanelComponent
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

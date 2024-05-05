import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AnalystPanelRoutingModule} from "./analyst-panel-routing.module";
import { AnalystPanelComponent } from './components/analyst-panel/analyst-panel.component';



@NgModule({
  declarations: [
    AnalystPanelComponent
  ],
  imports: [
    CommonModule,
    AnalystPanelRoutingModule
  ]
})
export class AnalystPanelModule {}

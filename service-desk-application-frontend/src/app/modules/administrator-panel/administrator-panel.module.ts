import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AdministratorPanelRoutingModule} from "./administrator-panel-routing.module";
import { AdministratorPanelComponent } from './components/administrator-panel/administrator-panel.component';

@NgModule({
  declarations: [
    AdministratorPanelComponent
  ],
  imports: [
    CommonModule,
    AdministratorPanelRoutingModule
  ]
})
export class AdministratorPanelModule { }

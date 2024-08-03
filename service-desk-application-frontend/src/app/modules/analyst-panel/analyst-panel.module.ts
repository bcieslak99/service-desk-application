import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AnalystPanelRoutingModule} from "./analyst-panel-routing.module";
import { AnalystPanelComponent } from './components/analyst-panel/analyst-panel.component';
import {MatTreeModule} from "@angular/material/tree";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {FormsModule} from "@angular/forms";



@NgModule({
  declarations: [
    AnalystPanelComponent
  ],
  imports: [
    CommonModule,
    AnalystPanelRoutingModule,
    MatTreeModule,
    MatIconModule,
    MatButtonModule,
    MatInputModule,
    MatCardModule,
    FormsModule
  ]
})
export class AnalystPanelModule {}

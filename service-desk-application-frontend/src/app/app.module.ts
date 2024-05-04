import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {RouterOutlet} from "@angular/router";
import { NavbarComponent } from './components/navbar/navbar.component';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {NotifierModule, NotifierOptions} from "angular-notifier";
import {MatButtonModule} from "@angular/material/button";
import { GlobalSpinnerComponent } from './components/global-spinner/global-spinner.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {GlobalSpinnerInterceptor} from "./guards_and_interceptors/global-spinner.interceptor";
import {AuthorizationInterceptor} from "./guards_and_interceptors/authorization.interceptor";

const notifierConfig: NotifierOptions = {
  theme: "material"
}

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    GlobalSpinnerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    RouterOutlet,
    MatToolbarModule,
    MatIconModule,
    HttpClientModule,
    NotifierModule.withConfig(notifierConfig),
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: GlobalSpinnerInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: AuthorizationInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

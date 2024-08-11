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
import { AccessDeniedViewComponent } from './components/access-denied-view/access-denied-view.component';
import {MatMenuModule} from "@angular/material/menu";
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from "@angular/material/core";
import {CustomDateAdapter} from "./adapters/month-adapter";

const notifierConfig: NotifierOptions = {
  theme: "material"
}

const MY_FORMATS = {
  parse: {
    dateInput: 'DD.MM.YYYY',
  },
  display: {
    dateInput: 'DD.MM.YYYY',
    monthYearLabel: 'MMMM YYYY',
    dateA11yLabel: 'DD.MM.YYYY',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    GlobalSpinnerComponent,
    AccessDeniedViewComponent
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
        MatProgressSpinnerModule,
        MatMenuModule
    ],
  providers: [
    { provide: DateAdapter, useClass: CustomDateAdapter },
    {provide: HTTP_INTERCEPTORS, useClass: GlobalSpinnerInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: AuthorizationInterceptor, multi: true},
    { provide: MAT_DATE_LOCALE, useValue: 'pl-PL' },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}

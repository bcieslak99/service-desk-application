import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {finalize, Observable} from 'rxjs';
import {GlobalSpinnerService} from "../services/global-spinner.service";

@Injectable()
export class GlobalSpinnerInterceptor implements HttpInterceptor
{
  constructor(private spinnerService: GlobalSpinnerService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>>
  {
    this.spinnerService.showSpinner();
    return next.handle(request).pipe(finalize(() => this.spinnerService.hideSpinner()));
  }
}

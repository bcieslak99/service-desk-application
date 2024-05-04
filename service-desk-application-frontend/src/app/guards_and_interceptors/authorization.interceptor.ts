import {inject, Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import {AuthService} from "../services/auth.service";

@Injectable()
export class AuthorizationInterceptor implements HttpInterceptor
{
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>>
  {
    if(inject(AuthService).userLogged != null)
    {
      let token = inject(AuthService).userLogged?.token.token;

      const newReq = request.clone({
        setHeaders: {
          'Authorization': token != undefined ? token : ""
        }
      });

      return next.handle(newReq);
    }

    return next.handle(request);
  }
}

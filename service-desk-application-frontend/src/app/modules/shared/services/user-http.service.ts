import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserAsListElement} from "../../../models/user-data.interfaces";
import {ApplicationSettings} from "../../../application-settings";

@Injectable({
  providedIn: 'root'
})
export class UserHttpService
{
  constructor(private http: HttpClient) {}

  getActiveUsers()
  {
    return this.http.get<UserAsListElement[]>(ApplicationSettings.apiUrl + "/api/v1/user/list/active");
  }
}

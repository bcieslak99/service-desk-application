import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GlobalSpinnerService
{
  isLoading = new BehaviorSubject<boolean>(false);

  showSpinner(): void
  {
    this.isLoading.next(true);
  }

  hideSpinner(): void
  {
    this.isLoading.next(false);
  }
}

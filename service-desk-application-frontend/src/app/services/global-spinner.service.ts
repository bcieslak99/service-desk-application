import {Injectable, NgZone} from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GlobalSpinnerService
{
  isLoading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  showSpinner(): void {
    setTimeout(() => {
      this.isLoading.next(true);
    });
  }

  hideSpinner(): void {
    setTimeout(() => {
      this.isLoading.next(false);
    });
  }
}

import { Component } from '@angular/core';
import {GlobalSpinnerService} from "../../services/global-spinner.service";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-global-spinner',
  templateUrl: './global-spinner.component.html',
  styleUrls: ['./global-spinner.component.css']
})
export class GlobalSpinnerComponent
{
  isLoading: BehaviorSubject<boolean> = this.spinnerService.isLoading;

  constructor(private spinnerService: GlobalSpinnerService) {}
}

import { Component } from '@angular/core';
import {GlobalSpinnerService} from "../../services/global-spinner.service";

@Component({
  selector: 'app-global-spinner',
  templateUrl: './global-spinner.component.html',
  styleUrls: ['./global-spinner.component.css']
})
export class GlobalSpinnerComponent
{
  isLoading = this.spinnerService.isLoading;

  constructor(private spinnerService: GlobalSpinnerService) {}
}

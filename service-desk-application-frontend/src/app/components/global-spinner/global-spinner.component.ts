import {AfterViewInit, ChangeDetectorRef, Component} from '@angular/core';
import {GlobalSpinnerService} from "../../services/global-spinner.service";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-global-spinner',
  templateUrl: './global-spinner.component.html',
  styleUrls: ['./global-spinner.component.css']
})
export class GlobalSpinnerComponent implements AfterViewInit
{
  isLoading: BehaviorSubject<boolean>;

  constructor(
    private spinnerService: GlobalSpinnerService,
    private cdRef: ChangeDetectorRef
  ) {
    this.isLoading = this.spinnerService.isLoading;
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.cdRef.detectChanges();
    }, 0);
  }
}

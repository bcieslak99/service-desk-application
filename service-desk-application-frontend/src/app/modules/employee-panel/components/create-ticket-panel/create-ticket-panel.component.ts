import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {EmployeePanelService} from "../../services/employee-panel.service";
import {EmployeeTicketForm, TicketCategory} from "../../../../models/ticket.interfaces";
import {NotifierService} from "angular-notifier";
import {UserAsListElement} from "../../../../models/user-data.interfaces";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {
  SelectingCategoryDialogComponent
} from "../../../shared/components/dialogs/selecting-category-dialog/selecting-category-dialog.component";
import {
  SelectingUserDialogComponent
} from "../../../shared/components/dialogs/selecting-user-dialog/selecting-user-dialog.component";
import {ServerResponsesMessage} from "../../../../models/server-responses.interfaces";

@Component({
  selector: 'app-create-ticket-panel',
  templateUrl: './create-ticket-panel.component.html',
  styleUrls: ['./create-ticket-panel.component.css']
})
export class CreateTicketPanelComponent implements OnInit
{
  @Input() ticketType: string | null = "SERVICE_REQUEST";
  category: TicketCategory | null = null;
  customer: UserAsListElement | null = null;
  categories: TicketCategory[] = [];
  customers: UserAsListElement[] = [];

  registerForm: FormGroup = new FormGroup({
    description: new FormControl("", [Validators.required, Validators.minLength(3), Validators.maxLength(3000)])
  });

  constructor(private http: EmployeePanelService, private notifier: NotifierService, private router: Router, private dialog: MatDialog) {}

  ticketTypeIsCorrect(): boolean
  {
    return this.ticketType !== null && (this.ticketType === "INCIDENT" || this.ticketType === "SERVICE_REQUEST");
  }

  getCardTitle(): string | null
  {
    if(this.ticketType === "INCIDENT")
      return "Rejestracja incydentu"
    else if(this.ticketType == "SERVICE_REQUEST")
      return "Rejestracja wniosku o usługę"
    else return null;
  }

  redirectToEmployeePanel(): void
  {
    this.router.navigate(['/employee/panel'])
  }

  private loadCategories(): void
  {
    if(this.ticketType !== null)
      this.http.getTicketCategories(this.ticketType).subscribe({
        next: categories => {
          this.categories = categories;
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się pobrać listy kategorii!");
          this.redirectToEmployeePanel();
        }
      });
  }

  private loadCustomers(): void
  {
    this.http.getCustomers().subscribe({
      next: customers => {
        this.customers = customers;
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać listy użytkowników!");
        this.redirectToEmployeePanel();
      }
    });
  }

  ngOnInit(): void
  {
    this.loadCategories();
    this.loadCustomers();
  }

  dataIsCorrect(): boolean
  {
    return this.category !== null && this.customer !== null && !this.registerForm.invalid;
  }

  openDialogToSelectCategory(): void
  {
    const dialog = this.dialog.open(SelectingCategoryDialogComponent, {
      data: this.categories
    })

    dialog.afterClosed().subscribe(result => {
      if(result !== null && result !== undefined)
        this.category = result;
    });
  }

  openDialogToSelectUser(): void
  {
    const dialog = this.dialog.open(SelectingUserDialogComponent, {
      data: this.customers
    })

    dialog.afterClosed().subscribe(result => {
      if(result !== null && result !== undefined)
        this.customer = result;
    });
  }

  registerTicket(): void
  {
    if(this.customer === null)
    {
      this.notifier.notify("error", "Musisz wybrać użytkownika!");
      return;
    }

    if(this.category === null)
    {
      this.notifier.notify("error", "Musisz wybrać kategorię!");
      return;
    }

    if(this.registerForm.invalid)
    {
      this.notifier.notify("error", "Musisz prawidłowo uzupełnić opis zgłoszenia!");
      return;
    }

    let formData = this.registerForm.getRawValue();

    let ticketData: EmployeeTicketForm = {
      description: formData.description,
      customer: this.customer.userId,
      category: this.category.categoryId
    }

    this.http.registerTicketAsEmployee(ticketData).subscribe({
      next: value =>  {
        this.notifier.notify("success", "Twoje zgłoszenie zostało zarejestrowane.");
        this.router.navigate(["/employee/panel"]);
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
      }
    });
  }
}

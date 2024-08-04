import {Component, Input, OnInit} from '@angular/core';
import {AnalystTicketForm, EmployeeTicketForm, TicketCategory} from "../../../../../models/ticket.interfaces";
import {UserAsListElement} from "../../../../../models/user-data.interfaces";
import {NotifierService} from "angular-notifier";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {TicketHttpService} from "../../../../shared/services/ticket-http.service";
import {
  SelectingCategoryDialogComponent
} from "../../../../shared/components/dialogs/selecting-category-dialog/selecting-category-dialog.component";
import {
  SelectingUserDialogComponent
} from "../../../../shared/components/dialogs/selecting-user-dialog/selecting-user-dialog.component";
import {ServerResponsesMessage} from "../../../../../models/server-responses.interfaces";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-register-ticket',
  templateUrl: './register-ticket.component.html',
  styleUrls: ['./register-ticket.component.css']
})
export class RegisterTicketComponent implements OnInit
{
 @Input() ticketType: string | null = null;
  category: TicketCategory | null = null;
  customer: UserAsListElement | null = null;
  reporter: UserAsListElement | null = null;
  categories: TicketCategory[] = [];
  users: UserAsListElement[] = [];

  registerForm: FormGroup = new FormGroup({
    description: new FormControl("", [Validators.required, Validators.minLength(3), Validators.maxLength(3000)])
  });

  constructor(private http: TicketHttpService, private notifier: NotifierService, private router: Router, private dialog: MatDialog) {}

  ticketTypeIsCorrect(): boolean
  {
    return this.ticketType !== null && (this.ticketType === "INCIDENT" || this.ticketType === "SERVICE_REQUEST");
  }

  dataIsCorrect(): boolean
  {
    return this.category !== null && this.customer !== null && this.reporter !== null && !this.registerForm.invalid;
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
      this.http.getCategoriesToRegisterTicket(this.ticketType).subscribe({
        next: categories => {
          this.categories = categories;
        },
        error: err => {
          this.notifier.notify("error", "Nie udało się pobrać listy kategorii!");
          this.redirectToEmployeePanel();
        }
      });
  }

  private loadUsers(): void
  {
    this.http.getActiveUsers().subscribe({
      next: users => {
        this.users = users;
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać listy użytkowników!");
        this.redirectToEmployeePanel();
      }
    });
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

  openDialogToSelectCustomer(): void
  {
    const dialog = this.dialog.open(SelectingUserDialogComponent, {
      data: this.users
    })

    dialog.afterClosed().subscribe(result => {
      if(result !== null && result !== undefined)
        this.customer = result;
    });
  }

  openDialogToSelectReporter(): void
  {
    const dialog = this.dialog.open(SelectingUserDialogComponent, {
      data: this.users
    })

    dialog.afterClosed().subscribe(result => {
      if(result !== null && result !== undefined)
        this.reporter = result;
    });
  }

  registerTicket(): void
  {
    if(this.customer === null)
    {
      this.notifier.notify("error", "Musisz wybrać użytkownika!");
      return;
    }

    if(this.reporter === null)
    {
      this.notifier.notify("error", "Musisz wybrać zgłaszającego!");
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

    let ticketData: AnalystTicketForm = {
      description: formData.description,
      customer: this.customer.userId,
      reporter: this.reporter.userId,
      category: this.category.categoryId
    }

    this.http.registerTicketAsAnalyst(ticketData).subscribe({
      next: value =>  {
        this.notifier.notify("success", "Twoje zgłoszenie zostało zarejestrowane.");
        this.router.navigate(["/analyst/panel"]);
      },
      error: err => {
        let result: ServerResponsesMessage = err.error as ServerResponsesMessage;
        this.notifier.notify("error", result.message);
      }
    });
  }

  ngOnInit(): void
  {
    this.loadCategories();
    this.loadUsers();
  }
}

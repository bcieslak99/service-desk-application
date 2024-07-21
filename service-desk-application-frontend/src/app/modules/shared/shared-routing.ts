import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {TicketDetailsComponent} from "./components/ticket-details/ticket-details.component";

const routes: Routes = [
    {
        path: ":id",
        pathMatch: "full",
        component: TicketDetailsComponent,
        title: "Service Desk :: Podgląd Zgłoszenia"
    },
    {
        path: "**",
        redirectTo: ""
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class SharedRouting {}

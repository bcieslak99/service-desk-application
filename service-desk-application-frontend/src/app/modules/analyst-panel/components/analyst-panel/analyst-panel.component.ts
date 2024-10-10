import { Component } from '@angular/core';
import {NestedTreeControl} from "@angular/cdk/tree";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {AuthService} from "../../../../services/auth.service";
import {TicketHttpService} from "../../../shared/services/ticket-http.service";
import {NotifierService} from "angular-notifier";
import {Router} from "@angular/router";
import {NoteService} from "../services/note.service";
import {Note} from "../../../../models/note-interfaces";
import {DialogNoteEditorComponent} from "../dialog-note-editor/dialog-note-editor.component";
import {MatDialog} from "@angular/material/dialog";

interface TreeNode {
  name: string;
  children?: TreeNode[];
  action?: () => void;
}

@Component({
  selector: 'app-analyst-panel',
  templateUrl: './analyst-panel.component.html',
  styleUrls: ['./analyst-panel.component.css']
})
export class AnalystPanelComponent
{
  treeControl = new NestedTreeControl<TreeNode>(node => node.children);
  dataSource = new MatTreeNestedDataSource<TreeNode>();
  panel: string = 'MAIN_PANEL';
  MAIN_PANEL: string = 'MAIN_PANEL';
  MY_INCIDENT_IN_PROGRESS: string = 'MY_INCIDENT_IN_PROGRESS';
  MY_INCIDENT_ON_HOLD: string = 'MY_INCIDENT_ON_HOLD';
  MY_INCIDENT_RESOLVED: string = 'MY_INCIDENT_RESOLVED';
  MY_SERVICE_REQUEST_IN_PROGRESS: string = 'MY_SERVICE_REQUEST_IN_PROGRESS';
  MY_SERVICE_REQUEST_ON_HOLD: string = 'MY_SERVICE_REQUEST_ON_HOLD';
  MY_SERVICE_REQUEST_RESOLVED: string = 'MY_SERVICE_REQUEST_RESOLVED';
  GROUP_INCIDENT_PENDING: string = 'GROUP_INCIDENT_PENDING';
  GROUP_INCIDENT_IN_PROGRESS: string = 'GROUP_INCIDENT_IN_PROGRESS';
  GROUP_INCIDENT_ON_HOLD: string = 'GROUP_INCIDENT_ON_HOLD';
  GROUP_INCIDENT_RESOLVED: string = 'GROUP_INCIDENT_RESOLVED';
  GROUP_SERVICE_REQUEST_PENDING: string = 'GROUP_SERVICE_REQUEST_PENDING';
  GROUP_SERVICE_REQUEST_IN_PROGRESS: string = 'GROUP_SERVICE_REQUEST_IN_PROGRESS';
  GROUP_SERVICE_REQUEST_ON_HOLD: string = 'GROUP_SERVICE_REQUEST_ON_HOLD';
  GROUP_SERVICE_REQUEST_RESOLVED: string = 'GROUP_SERVICE_REQUEST_RESOLVED'
  TASKS_OF_MY_GROUPS: string = 'TASKS_OF_MY_GROUPS';
  TASK_SET_CREATOR: string = "TASK_SET_CREATOR"
  TASK_SET_DETAILS: string = "TASK_SET_DETAILS";
  NOTES: string = 'NOTES';
  ticketId: string = '';
  taskSetId: string = ""

  treeData: TreeNode[] = [
    {
      name: 'Panel główny',
      action: () => this.panel = this.MAIN_PANEL
    },
    {
      name: 'Moje zgłoszenia',
      children: [
        {
          name: 'Incydenty',
          children: [
            {
              name: 'W trakcie',
              action: () => this.panel = this.MY_INCIDENT_IN_PROGRESS
            },
            {
              name: 'Wstrzymane',
              action: () => this.panel = this.MY_INCIDENT_ON_HOLD
            },
            {
              name: 'Rozwiązane',
              action: () => this.panel = this.MY_INCIDENT_RESOLVED
            }
          ]
        },
        {
          name: 'Wniosek',
          children: [
            {
              name: 'W trakcie',
              action: () => this.panel = this.MY_SERVICE_REQUEST_IN_PROGRESS
            },
            {
              name: 'Wstrzymane',
              action: () => this.panel = this.MY_SERVICE_REQUEST_ON_HOLD
            },
            {
              name: 'Rozwiązane',
              action: () => this.panel = this.MY_SERVICE_REQUEST_RESOLVED
            }
          ]
        }
      ]
    },
    {
      name: 'Zgłoszenia mojej grupy',
      children: [
        {
          name: 'Incyndenty',
          children: [
            {
              name: 'Oczekujące',
              action: () => this.panel = this.GROUP_INCIDENT_PENDING
            },
            {
              name: 'W trakcie',
              action: () => this.panel = this.GROUP_INCIDENT_IN_PROGRESS
            },
            {
              name: 'Wstrzymane',
              action: () => this.panel = this.GROUP_INCIDENT_ON_HOLD
            },
            {
              name: 'Rozwiązane',
              action: () => this.panel = this.GROUP_INCIDENT_RESOLVED
            }
          ]
        },
        {
          name: 'Wnioski',
          children: [
            {
              name: 'Oczekujące',
              action: () => this.panel = this.GROUP_SERVICE_REQUEST_PENDING
            },
            {
              name: 'W trakcie',
              action: () => this.panel = this.GROUP_SERVICE_REQUEST_IN_PROGRESS
            },
            {
              name: 'Wstrzymane',
              action: () => this.panel = this.GROUP_SERVICE_REQUEST_ON_HOLD
            },
            {
              name: 'Rozwiązane',
              action: () => this.panel = this.GROUP_SERVICE_REQUEST_RESOLVED
            }
          ]
        }
      ]
    },
    {
      name: 'Zadania mojej grupy',
      action: () => this.panel = this.TASKS_OF_MY_GROUPS
    },
    {
      name: 'Notatki',
      action: () => this.panel = this.NOTES
    }
  ];

  constructor(
    private auth: AuthService,
    private httpTicketService: TicketHttpService,
    private notifier: NotifierService,
    private router: Router,
    private noteService: NoteService,
    private dialog: MatDialog
  )
  {
    this.dataSource.data = this.treeData;
  }

  hasChild = (_: number, node: TreeNode) => !!node.children && node.children.length > 0;

  getTicketsOfGroups(ticketType: string, ticketStatus: string)
  {
    return this.httpTicketService.getTicketsOfGroups(ticketType, ticketStatus);
  }

  getTicketsOfUser(ticketType: string, ticketStatus: string)
  {
    return this.httpTicketService.getTicketsOfUser(ticketType, ticketStatus);
  }

  findTicket(): void
  {
    if(this.ticketId.trim().length < 1) return;

    this.httpTicketService.ticketExists(this.ticketId).subscribe({
      next: result =>  {
        if(result.ticketExists) this.router.navigate(["/ticket", this.ticketId]);
      },
      error: err => {
        this.notifier.notify("error", "Nie udało się pobrać informacji na temat zgłoszenia!");
      }
    })
  }

  openNoteEditor()
  {
    return this.dialog.open(DialogNoteEditorComponent, {data: {title: "", description: ""}});
  }

  createNote(): void
  {
    this.openNoteEditor().afterClosed().subscribe(result => {
      if(result !== null && result !== undefined) this.noteService.createNote(result).subscribe({
        next: note => {
          this.notifier.notify("success", "Notatka została utworzona.");
        },
        error: err => {
          this.notifier.notify("error", "Napotkano na błąd podczas tworzenia notatki!");
        }
      });
    });
  }

  showTaskSetDetails(taskSetId: string): void
  {
    this.panel = this.TASK_SET_DETAILS;
    this.taskSetId = taskSetId;
  }

  showTaskCreator(): void
  {
    if(this.userIsGroupManager())
      this.panel = this.TASK_SET_CREATOR;
  }

  userIsGroupManager(): boolean
  {
    return this.auth.userIsManager();
  }
}

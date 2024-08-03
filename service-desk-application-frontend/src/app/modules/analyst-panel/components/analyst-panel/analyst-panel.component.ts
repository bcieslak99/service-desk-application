import { Component } from '@angular/core';
import {NestedTreeControl} from "@angular/cdk/tree";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {AuthService} from "../../../../services/auth.service";

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
  CURRENT_STATE_OF_TICKETS: string = 'CURRENT_STATE_OF_TICKETS';
  MONTHLY_PERFORMANCE: string = 'MONTHLY_PERFORMANCE';
  POPULAR_CATEGORIES: string = 'POPULAR_CATEGORIES';
  GROUP_ACTIVITIES: string = 'GROUP_ACTIVITIES';
  MY_TASKS: string = 'MY_TASKS';
  TASKS_OF_MY_GROUPS: string = 'TASKS_OF_MY_GROUPS';
  NOTES: string = 'NOTES';
  ticketId: string = '';

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
      name: 'Zadania',
      children: [
        {
          name: 'Moje zadania',
          action: () => this.panel = this.MY_TASKS
        },
        {
          name: 'Zadania mojej grupy',
          action: () => this.panel = this.TASKS_OF_MY_GROUPS
        }
      ]
    },
    {
      name: 'Notatki',
      action: () => this.panel = this.NOTES
    },
    {
      name: 'Raporty',
      children: [
        {
          name: 'Obecny stan zgłoszeń',
          action: () => this.panel = this.CURRENT_STATE_OF_TICKETS
        },
        {
          name: 'Miesięczna realizacja',
          action: () => this.panel = this.MONTHLY_PERFORMANCE
        },
        {
          name: "Popularoność kategorii",
          action: () => this.panel = this.POPULAR_CATEGORIES
        },
        {
          name: 'Aktywność grup',
          action: () => this.panel = this.GROUP_ACTIVITIES
        }
      ]
    }
  ];

  constructor(private auth: AuthService)
  {
    this.dataSource.data = this.treeData;
  }

  hasChild = (_: number, node: TreeNode) => !!node.children && node.children.length > 0;
}

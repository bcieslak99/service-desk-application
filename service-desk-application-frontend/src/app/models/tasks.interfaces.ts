import {UserAsListElement} from "./user-data.interfaces";

export interface TaskSetAsListElement
{
  id: string,
  title: string,
  group: string,
  plannedEndDate: string
}

export  interface TaskForm
{
  description: string,
  position: number
}

export interface TaskSetForm
{
  title: string,
  plannedEndDate: Date,
  tasks: TaskForm[],
  supportGroupId: string
}

export interface Task
{
  id: string,
  description: string,
  done: boolean
}

export interface Comment
{
  id: string,
  comment: string,
  createdAt: Date,
  author: UserAsListElement
}

export interface TaskSet
{
  id: string,
  title: string,
  group: string,
  plannedEndDate: Date,
  tasks: Task[],
  comments: Comment[]
}

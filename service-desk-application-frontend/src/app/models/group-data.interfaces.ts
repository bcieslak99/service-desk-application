import {UserAsListElement} from "./user-data.interfaces";

export interface GroupData
{
  groupId: string,
  name: string,
  description: string,
  groupType: string,
  groupActive: boolean,
  manager: ManagerData | null
}

export interface ManagerData
{
  userId: string,
  name: string,
  surname: string,
  mail: string,
  phoneNumber: string | null,
  userActive: boolean
}

export interface NewGroupData
{
  name: string,
  description: string,
  groupType: string,
  groupActive: boolean,
  managerId: string | null
}

export interface Member
{
  id: string,
  name: string,
  surname: string,
  mail: string,
  phoneNumber: string | null,
  userActive: boolean
}

export interface GroupMembers
{
  addedUsers: Member[],
  otherUsers: Member[]
}

export interface MemberToModify
{
  id: string,
  name: string,
  surname: string,
  mail: string,
  phoneNumber: string | null,
  userActive: boolean,
  modify: string
}

export interface Members
{
  manager: UserAsListElement | null;
  members: UserAsListElement[]
}

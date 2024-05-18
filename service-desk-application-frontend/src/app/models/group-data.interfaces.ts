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

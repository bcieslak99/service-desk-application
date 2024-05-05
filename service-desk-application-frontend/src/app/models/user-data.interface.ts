export interface UserCredentials
{
  mail: string,
  password: string
}

export interface JWTToken
{
  token: string;
}

export interface AuthData
{
  name: string,
  surname: string,
  mail: string
  token: {
    token: string,
    expiration: number
  },
  refreshToken: {
    token: string,
    expiration: string
  },
  roles: string[]
}

export interface UserAsListElement
{
  userId: string,
  name: string,
  surname: string,
  mail: string,
  phoneNumber: string,
  userActive: boolean
}

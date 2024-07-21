interface BaseModel {
  id: string,
  publishUri: string,
  classPath: string,
  jarFileId: string,
  jarFileName: string,
  file: File | null,
  isActive: boolean
}

export interface Endpoint extends BaseModel {
  beanName: string
}

export interface Restful extends BaseModel {
}

export interface Response {
  id: string,
  publishUri: string,
  method: string,
  condition: string,
  responseContent: string,
  isActive: boolean
}

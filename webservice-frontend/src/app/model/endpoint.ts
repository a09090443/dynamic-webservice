export interface Endpoint {
  id: string,
  publishUri: string,
  beanName: string,
  classPath: string,
  jarFileId: string,
  jarFileName: string,
  file: File | null,
  isActive: boolean
}

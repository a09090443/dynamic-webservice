export interface Endpoint {
  rowId: string,
  publishUrl: string,
  beanName: string,
  classPath: string,
  jarFileId: string,
  jarFileName: string,
  file: File | null,
  isActive: boolean
}

export interface Endpoint {
  rowId: string,
  publishUrl: string,
  beanName: string,
  classPath: string,
  jarFileName: string,
  file: File | null,
  isActive: boolean
}

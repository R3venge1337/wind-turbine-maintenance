export interface PageDto<T> {
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  content: T[];
}
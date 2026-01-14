export enum SortDirection {
  ASC = 'ASC',
  DESC = 'DESC'
}

export interface PageableRequest {
  page: number;         // Odpowiada @Min(1)
  size: number;         // Odpowiada @Min(1)
  sortField?: string;
  sortDirection?: SortDirection;
}

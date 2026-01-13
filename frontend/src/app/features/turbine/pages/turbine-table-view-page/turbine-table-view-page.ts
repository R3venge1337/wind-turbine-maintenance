import { Component, effect, inject, signal } from '@angular/core';
import { TurbineTableComponent } from "../../components/turbine-table-component/turbine-table-component";
import { PageableRequest, SortDirection } from '../../../../shared/models/pageRequest';
import { TurbineView, FilterTurbineForm } from '../../models/turbine';
import { TurbineService } from '../../services/turbine-service';
import { PageDto } from '../../../../shared/models/pageDto';
import { TurbineTableFilterComponent } from "../../components/turbine-table-filter-component/turbine-table-filter-component";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-turbine-table-view-page',
  imports: [TurbineTableComponent, TurbineTableFilterComponent, TranslateModule],
  templateUrl: './turbine-table-view-page.html',
  styleUrl: './turbine-table-view-page.scss',
})
export class TurbineTableViewPage {
private turbineService = inject(TurbineService);

  // Sygnały stanu strony
  turbines = signal<TurbineView[]>([]);
  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  
  // Domyślne filtry (puste)
  filters = signal<FilterTurbineForm>({});

  constructor() {
    effect(() => {
      this.loadData();
    }, { allowSignalWrites: true });
  }

  ngOnInit() {
    
  }

  loadData() {
    const pageable: PageableRequest = {
      page: this.currentPage() + 1,
      size: this.pageSize(),
      sortField: 'id',
      sortDirection: SortDirection.ASC
    };

    this.turbineService.findAllActiveTurbines(this.filters(), pageable)
      .subscribe({
        next: (response: PageDto<TurbineView>) => {
          console.log(response)
          this.turbines.set(response.content);
          this.totalPages.set(response.pageNumber);
        },
        error: (err) => console.error('Błąd ładowania tabeli:', err)
      });
  }

  changePage(delta: number) {
    this.currentPage.update(p => p + delta);
  }

  handleReset(id: number) {
    console.log('Reset turbiny o ID:', id);
  }

    onFilter(newFilters: any) {
    this.currentPage.set(0);
    this.filters.set(newFilters);
  }
}


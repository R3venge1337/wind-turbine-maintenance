import { CommonModule } from '@angular/common';
import { Component, inject, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-turbine-table-filter-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslateModule],
  templateUrl: './turbine-table-filter-component.html',
  styleUrl: './turbine-table-filter-component.scss',
})
export class TurbineTableFilterComponent {
private fb = inject(FormBuilder);
  
  // Emituje obiekt FilterTurbineForm przy każdej zmianie
  filterChanged = output<any>();

  filterForm = this.fb.group({
    productId: [''],
    typeCode: [''],
    severity: [''],
    city: [''],
    minToolWear: [null as number | null],
    maxToolWear: [null as number | null]
  });

  applyFilters() {
    this.filterChanged.emit(this.filterForm.value);
  }

  resetFilters() {
    this.filterForm.reset();
    this.applyFilters();
  }
}

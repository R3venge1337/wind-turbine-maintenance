import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { TurbineView } from '../../models/turbine';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-turbine-table-component',
  standalone: true,
  imports: [CommonModule,RouterLink, TranslateModule],
  templateUrl: './turbine-table-component.html',
  styleUrl: './turbine-table-component.scss',
})
export class TurbineTableComponent {
turbines = input.required<TurbineView[]>(); 
showDetails = output<TurbineView>();
resetRequest = output<number>();

  onReset(id: number) {
    this.resetRequest.emit(id);
  }

  getSeverityClass(turbine: TurbineView): string {
    return `status-${(turbine.mainSeverity || 'GOOD').toLowerCase()}`;
  }
  onShowDetails(turbine: TurbineView) {
  this.showDetails.emit(turbine);
}
  
}

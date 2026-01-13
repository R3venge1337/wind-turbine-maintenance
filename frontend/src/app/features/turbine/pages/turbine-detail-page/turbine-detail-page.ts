import { Component, inject, OnInit, signal } from '@angular/core';
import { TurbineDetailComponent } from "../../components/turbine-detail-component/turbine-detail-component";
import { ActivatedRoute } from '@angular/router';
import { TurbineService } from '../../services/turbine-service';
import { MeasurementDto } from '../../../wind-turbine-map/models/measurementDto';
import { TurbineView } from '../../models/turbine';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-turbine-detail-page',
  standalone: true,
  imports: [TurbineDetailComponent, RouterLink, TranslateModule],
  templateUrl: './turbine-detail-page.html',
  styleUrl: './turbine-detail-page.scss',
})
export class TurbineDetailPage implements OnInit{
  private route = inject(ActivatedRoute);
  private turbineService = inject(TurbineService);
  turbine = signal<TurbineView | null>(null);
  measurements = signal<MeasurementDto[]>([]);
  isLoading = signal(true);

  ngOnInit() {
    const productId = String(this.route.snapshot.paramMap.get('productId'));
    if (productId) {
      this.loadAllData(productId);
    }
  }

  loadAllData(productId: string) {
    this.turbineService.findTurbineByProductId(productId).subscribe(data => {
      this.turbine.set(data);
    });

    this.turbineService.getLatestMeasurements(productId).subscribe(data => {
      this.measurements.set(data);
      this.isLoading.set(false);
    });
  }

}

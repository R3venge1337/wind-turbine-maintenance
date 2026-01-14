import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'map', pathMatch: 'full' },
  {
     path: 'map', loadComponent: () => import('./features/wind-turbine-map/components/wind-turbine-map/wind-turbine-map').then(m => m.WindTurbineMap)
  },
  {
     path: 'turbines', loadComponent: () => import('./features/turbine/pages/turbine-table-view-page/turbine-table-view-page').then(m => m.TurbineTableViewPage)
  },
  {
    path: 'turbines/:productId', loadComponent: () => import('./features/turbine/pages/turbine-detail-page/turbine-detail-page').then(m => m.TurbineDetailPage)
  }
];

import {Routes} from '@angular/router';
import {EndpointComponent} from "./endpoint/endpoint.component";
import {TableComponent} from "./table/table.component";

export const routes: Routes = [
  {
    path: 'endpoint',
    component: EndpointComponent,
  },
  {
    path: 'table',
    component: TableComponent,
  }
];

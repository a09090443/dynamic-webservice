import {Routes} from '@angular/router';
import {EndpointComponent} from "./endpoint/endpoint.component";
import {ResponseComponent} from "./response/response.component";

export const routes: Routes = [
  {
    path: "",
    redirectTo: "/endpoint",
    pathMatch: "full",
  },
  {
    path: 'endpoint',
    component: EndpointComponent,
  },
  {
    path: 'response',
    component: ResponseComponent,
  },
  {path: '**', redirectTo: '/endpoint'}
];

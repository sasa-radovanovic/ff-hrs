import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RequestOverviewComponent } from './request-overview/request-overview.component';
import { MyRequestsComponent } from './my-requests/my-requests.component';
import {RouterModule, Routes} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MaterialModule} from "../material/material.module";
import { PendingRequestsComponent } from './request-overview/pending-requests/pending-requests.component';
import { ApprovedRequestsComponent } from './request-overview/approved-requests/approved-requests.component';
import {AdminGuardGuard} from "../guards/admin-guard.guard";
import {RequestService} from "../services/request.service";
import { UserRequestsComponent } from './my-requests/user-requests/user-requests.component';
import { TeamRequestsComponent } from './my-requests/team-requests/team-requests.component';




const routes: Routes = [
  {
    canActivate: [ AdminGuardGuard ],
    path: 'request-overview',
    component: RequestOverviewComponent
  },
  {
    path: 'my-requests',
    component: MyRequestsComponent
  }
];



@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes, {useHash: true})
  ],
  providers: [
    AdminGuardGuard,
    RequestService
  ],
  declarations: [RequestOverviewComponent, MyRequestsComponent, PendingRequestsComponent, ApprovedRequestsComponent, UserRequestsComponent, TeamRequestsComponent]
})
export class RequestsModule { }

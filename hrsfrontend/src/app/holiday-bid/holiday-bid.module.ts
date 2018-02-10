import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HolidayBidOverviewComponent } from './holiday-bid-overview/holiday-bid-overview.component';
import { HolidayBidComponent } from './holiday-bid/holiday-bid.component';
import {RouterModule, Routes} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MaterialModule} from "../material/material.module";
import {RequestService} from "../services/request.service";


const routes: Routes = [
  {
    path: 'new',
    component: HolidayBidComponent
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
  declarations: [HolidayBidOverviewComponent, HolidayBidComponent],
  providers: [
    RequestService
  ]
})
export class HolidayBidModule { }

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { HttpClientModule } from '@angular/common/http'

import { Routes, RouterModule } from '@angular/router';

import { TeamModule } from './team/team.module';
import { HolidayBidModule } from './holiday-bid/holiday-bid.module';
import { LoginModule } from './login/login.module';

import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';

import { MaterialModule } from './material/material.module'

import { FlexLayoutModule } from "@angular/flex-layout";

import { RequestsModule } from './requests/requests.module';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent
  ],
  imports: [
    BrowserModule,
    FlexLayoutModule,
    FormsModule,
    HttpClientModule,
    TeamModule,
    HolidayBidModule,
    RequestsModule,
    LoginModule,
    BrowserAnimationsModule,
    MaterialModule,
    RouterModule.forRoot(routes, {useHash: true})
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

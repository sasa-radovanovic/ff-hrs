import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeamOverviewComponent } from './team-overview/team-overview.component';
import { NewTeamComponent } from './new-team/new-team.component';
import { EditUserComponent } from './edit-user/edit-user.component';
import { Routes, RouterModule } from '@angular/router';
import { UserOverviewComponent } from './user-overview/user-overview.component';
import { AdminGuardGuard } from '../guards/admin-guard.guard';
import { UserService } from '../services/user.service';
import { TeamDetailsComponent } from './team-details/team-details.component';
import { MaterialModule } from './../material/material.module';

import { NewUserDialog } from './team-details/team-details.component';
import { ConfirmDialog } from './edit-user/edit-user.component'
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NewUserComponent } from './new-user/new-user.component';


const routes: Routes = [
  {
    path: 'team-overview',
    canActivate: [ AdminGuardGuard ],
    children: [
      {path: '', component: TeamOverviewComponent},
      {path: 'new', component: NewTeamComponent},
      {path: 'team-details/:name', component: TeamDetailsComponent}
    ]
  },
  {
    path: 'user-overview',
    canActivate: [ AdminGuardGuard ],
    children: [
      {path: '', component: UserOverviewComponent},
      {path: 'new', component: NewUserComponent},
      {path: 'edit/:username', component: EditUserComponent},
    ]
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
  declarations: [TeamOverviewComponent, NewTeamComponent, EditUserComponent, UserOverviewComponent, TeamDetailsComponent, NewUserDialog, ConfirmDialog, NewUserComponent],
  providers: [
    AdminGuardGuard,
    UserService
  ],
  entryComponents: [NewUserDialog, ConfirmDialog]
})
export class TeamModule { }

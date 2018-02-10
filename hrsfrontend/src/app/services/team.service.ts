import {EventEmitter, Injectable} from '@angular/core';

import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import 'rxjs/add/operator/catch'
import 'rxjs/add/operator/do'
import 'rxjs/add/observable/throw'
import 'rxjs/add/observable/of';

@Injectable()
export class TeamService {


    apiRoot: string = "http://localhost:8087/api/teams";

    constructor(private _http: HttpClient) {
    }


    private handleError(err: HttpErrorResponse) {
        return Observable.throw(err)
    }


    getAllTeams(offset: number) {
        console.log('Retrieve teams with offset ' + offset);
        const url = `${this.apiRoot}/admin/all/${offset}`;
        return this._http.get(url)
            .do(data => {
                console.log('Retrieved teams from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    getTeamDetails(team: string) {
        console.log('Retrieve team details' + team);
        const url = `${this.apiRoot}/details/${team}`;
        return this._http.get(url)
            .do(data => {
                console.log('Retrieved team details from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    newTeamMemberPartialSearch(criteria: string, team: string) {
        console.log('Retrieve partial search ' + criteria + ' team ' + team);
        const url = `${this.apiRoot}/details/new-team-member/${team}?criteria=${criteria}`;
        return this._http.get(url)
            .do(data => {
                console.log('Retrieved partial search from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    addUserToTeam(username: string, team: string) {
        console.log('Add user to team ' + username + ' team ' + team);
        const url = `${this.apiRoot}/admin/add-user`;
        return this._http.post(url, {
            username: username,
            team: team
        })
            .do(data => {
                console.log('Add user to team from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    addTeamAdminToTeam(username: string, team: string) {
        console.log('Add user to team ' + username + ' team ' + team);
        const url = `${this.apiRoot}/admin/add-team-admin`;
        return this._http.post(url, {
            username: username,
            team: team
        })
            .do(data => {
                console.log('Add team admin to team from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    removeUserFromTeam (team, user) {
        console.log('Remove user from team ' + user + ' team ' + team);
        const url = `${this.apiRoot}/admin/remove-user`;
        return this._http.post(url, {
            username: user,
            team: team
        })
            .do(data => {
                console.log('Removed user from team from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    removeTeamAdminFromTeam (team, user) {
        console.log('Remove team admin from team ' + user + ' team ' + team);
        const url = `${this.apiRoot}/admin/remove-team-admin`;
        return this._http.post(url, {
            username: user,
            team: team
        })
            .do(data => {
                console.log('Removed team admin from team from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    deleteTeam (team) {
        console.log('Delete team ' + team);
        const url = `${this.apiRoot}/admin/delete/${team}`;
        return this._http.delete(url)
            .do(data => {
                console.log('Deleted team from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    checkNameTaken(name) {
        console.log('Partial find team ' + name);
        const url = `${this.apiRoot}/admin/check-name/${name}`;
        return this._http.get(url)
            .do(data => {
                console.log('Partial find response team from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    createTeam(name) {
        console.log('Create team ' + name);
        const url = `${this.apiRoot}/admin/create`;
        return this._http.post(url, {
            name
        })
            .do(data => {
                console.log('Create team from BE >>> ', data)
            })
            .catch(this.handleError)
    }




}

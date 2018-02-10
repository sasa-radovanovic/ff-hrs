import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable()
export class RequestService {

    apiRoot: string = "http://localhost:8087/api/request";


    constructor(private _http: HttpClient) {
    }

    private handleError(err: HttpErrorResponse) {
        return Observable.throw(err)
    }


    approveRequestByAdmin(id) {
        console.log('Approve request ' + id);
        const url = `${this.apiRoot}/admin/req/${id}`;
        return this._http.post(url, {})
            .do(data => {
                console.log('Approved request BE >>> ', data)
            })
            .catch(this.handleError)
    }


    approveRequestByTeamAdmin(id) {

    }

    deleteRequest(id) {
        console.log('Delete request ' + id);
        const url = `${this.apiRoot}/admin/req/${id}`;
        return this._http.delete(url, {})
            .do(data => {
                console.log('Deleted request BE >>> ', data)
            })
            .catch(this.handleError)
    }


    allPendingRequests(): Observable<Object[]> {
        console.log('Pending requests ');
        const url = `${this.apiRoot}/admin/req/pending`;
        return this._http.get(url)
            .do(data => {
                console.log('Pending requests BE >>> ', data)
            })
            .catch(this.handleError)
    }


    allApprovedRequests(): Observable<Object[]> {
        console.log('Approved requests ');
        const url = `${this.apiRoot}/admin/req/approved`;
        return this._http.get(url)
            .do(data => {
                console.log('Approved requests BE >>> ', data)
            })
            .catch(this.handleError)
    }


    getMyRequests(): Observable<Object> {
        const url = `${this.apiRoot}/req/my-requests`;
        return this._http.get(url)
            .do(data => {
                console.log('My requests BE >>> ', data)
            })
            .catch(this.handleError)
    }


    validateDates(startDate: string, endDate: string): Observable<Object> {
        const url = `${this.apiRoot}/req/validate-dates?startDate=${startDate}&endDate=${endDate}`;
        return this._http.get(url)
            .do(data => {
                console.log('Validate dates BE >>> ', data)
            })
            .catch(this.handleError);
    }


    newRequest(startDate: string, endDate: string): Observable<Object> {
        const url = `${this.apiRoot}/req/new`;
        return this._http.post(url, {
            startDate,
            endDate
        })
            .do(data => {
                console.log('New request BE >>> ', data)
            })
            .catch(this.handleError);
    }

    deleteUserRequest(id: string): Observable<Object> {
        console.log('Delete request ' + id);
        const url = `${this.apiRoot}/req/${id}`;
        return this._http.delete(url, {})
            .do(data => {
                console.log('Deleted request BE >>> ', data)
            })
            .catch(this.handleError)
    }

}

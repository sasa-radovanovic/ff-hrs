import {EventEmitter, Injectable} from '@angular/core';

import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/catch'
import 'rxjs/add/operator/do'
import 'rxjs/add/observable/throw'
import 'rxjs/add/observable/of';


@Injectable()
export class UserService {

    user: Object;
    userChange: EventEmitter<any>;

    apiRoot: string = "http://localhost:8087/api";

    constructor(private _http: HttpClient) {
        this.userChange = new EventEmitter();
    }

    private handleError(err: HttpErrorResponse) {
        return Observable.throw(err)
    }

    justEmit(data) {
        this.emitUserData();
    }

    getUserChangeEmitter() {
        return this.userChange;
    }

    emitUserData() {
        console.log("Emit user change", this.user)
        this.userChange.emit(this.user);
    }

    login(username, password): Observable<Object> {
        const url = `${this.apiRoot}/auth/login`;
        return this._http.post(url, {
            username: username,
            password: password
        })
            .do(data => console.log('Successful login >>> ', data))
            .catch(this.handleError)

    }


    logout(): Observable<Object> {
        const url = `${this.apiRoot}/auth/user/logout`;
        return this._http.get(url)
            .do(data => console.log('Successful logout >>> ', data))
            .catch(this.handleError)

    }


    retrieveUser(): Observable<Object> {
        if (this.user !== undefined) {
            console.log('Cached user data >>> ', this.user)
            this.emitUserData();
            return Observable.of(this.user);
        } else {
            const url = `${this.apiRoot}/auth/user/data`;
            return this._http.get(url)
                .do(data => {
                    console.log('Retrieved user data from BE >>> ', data)
                    this.user = data;
                    this.emitUserData();
                })
                .catch(this.handleError)
        }
    }


    partialSearch(criteria: string): Observable<any> {
        const url = `${this.apiRoot}/user/partial-search?criteria=${criteria}`;
        return this._http.get(url)
            .do(data => {
                console.log('Retrieved partial user search from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    detailedUserData(username): Observable<any> {
        const url = `${this.apiRoot}/user/data/${username}`;
        return this._http.get(url)
            .do(data => {
                console.log('Retrieved user detailed data from BE >>> ', data)
            })
            .catch(this.handleError)
    }


    updateUser(username, firstName, lastName, email) {
        const url = `${this.apiRoot}/user/create-edit`;
        return this._http.post(url, {
            username,
            firstName,
            lastName,
            email
        })
            .do(data => {
                console.log('User updated BE >>> ', data)
            })
            .catch(this.handleError)
    }


    deleteUser(username) {
        const url = `${this.apiRoot}/user/remove/${username}`;
        return this._http.delete(url)
            .do(data => {
                console.log('User deleted BE >>> ', data)
            })
            .catch(this.handleError)
    }


    checkEmailIsInUse(criteria: string) {
        const url = `${this.apiRoot}/user/check-email/${criteria}`;
        return this._http.get(url)
            .do(data => {
                console.log('Check email BE >>> ', data)
            })
            .catch(this.handleError)
    }

    checkUsernameIsInUse(criteria: string) {
        const url = `${this.apiRoot}/user/check-username/${criteria}`;
        return this._http.get(url)
            .do(data => {
                console.log('Check username BE >>> ', data)
            })
            .catch(this.handleError)
    }


    validateActivation(hash: string, username: string) {
        const url = `${this.apiRoot}/auth/activation-hash?hash=${hash}&username=${username}`;
        return this._http.get(url)
            .do(data => {
                console.log('Check activation hash BE >>> ', data)
            })
            .catch(this.handleError)
    }


    setPassword(hash: string, username: string, password: string) {
        const url = `${this.apiRoot}/auth/set-password`;
        return this._http.post(url, {
            username,
            hash,
            password
        })
            .do(data => {
                console.log('Set password BE >>> ', data)
            })
            .catch(this.handleError)
    }

}

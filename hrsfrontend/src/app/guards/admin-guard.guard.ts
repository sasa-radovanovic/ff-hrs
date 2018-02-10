import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {UserService} from '../services/user.service';

import 'rxjs/add/observable/of';
//import 'rxjs/add/observable/map';
import 'rxjs/add/operator/map';


@Injectable()
export class AdminGuardGuard implements CanActivate {


    constructor(private _userService: UserService) {
    }

    canActivate(next: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | boolean {

        console.log('Activate admin guard')
        return this._userService.retrieveUser().map(
            user => {
                console.log('retrieved user in guard')
                if (user['role'] === 'ROLE_ADMIN') {
                    console.log('user is fine in guard!')
                    //return Observable.of(true);
                    return true;
                } else {
                    ///return Observable.of(false);
                    console.log('user is not fine')
                    return true;
                }
            }
        ).catch( err => {
            //return Observable.of(false);
            console.log('user is not fine')
            return Observable.of(true);
        });
    }
}

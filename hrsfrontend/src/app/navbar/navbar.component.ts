import { Component, Input, OnInit, SimpleChanges, OnChanges, OnDestroy } from '@angular/core';
import { UserService } from "../services/user.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnChanges, OnDestroy {

    @Input() user: Object;

    constructor(private _userService: UserService, private _router: Router) {
    }

    ngOnInit() {

    }

    ngOnDestroy() {
    }


    ngOnChanges(changes: SimpleChanges) {

        console.log('changes', changes['user'].currentValue);
        this.user = changes['user'].currentValue;

    }

    toTeams() {
        this._router.navigate(['team-overview']);
    }

    toUsers() {
        this._router.navigate(['user-overview']);
    }

    toRequests() {
        this._router.navigate(['request-overview']);
    }


    toMyRequests() {
        this._router.navigate(['my-requests']);
    }


    isSuperAdmin(): boolean {
        return true;
        /*if (this.user !== undefined && this.user['role'] !== undefined) {
            if (this.user['role'] === 'ROLE_ADMIN') {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }*/
    }


    logout() {
        alert('Logout procedure');
        this._userService.logout().subscribe(data => {
            this._router.navigate(['login']);
        })
    }

}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from './services/user.service';
import {TeamService} from './services/team.service'
import {Router} from "@angular/router";
import {RequestService} from "./services/request.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    providers: [UserService, TeamService, RequestService]
})
export class AppComponent implements OnInit, OnDestroy {

    subscription: any;
    user: Object;

    constructor(private _userService: UserService, private router: Router) {
        console.log("APP ON INIT");
        this.subscription = this._userService.getUserChangeEmitter()
            .subscribe(user => {
                    console.log('app component changed emitter caught');
                    this.user = user;
                },
                err => {
                    console.error("ERROR CATCHING THE CHANGE");
                });
    }

    ngOnInit() {

    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}

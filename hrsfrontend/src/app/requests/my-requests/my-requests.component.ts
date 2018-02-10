import {Component, OnInit, SimpleChanges} from '@angular/core';
import {RequestService} from "../../services/request.service";
import {MatSnackBar} from "@angular/material";
import {FormBuilder} from "@angular/forms";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";

@Component({
    selector: 'app-my-requests',
    templateUrl: './my-requests.component.html',
    styleUrls: ['./my-requests.component.css']
})
export class MyRequestsComponent implements OnInit {

    user: Object;
    requests: Object;
    myRequests: Object[];
    teamRequests: Object[];

    constructor(private _requestService: RequestService, private _userService: UserService, private router: Router, private fb: FormBuilder,
                public snackBar: MatSnackBar) {
    }

    ngOnInit() {
        this.user = this._userService.user;
        this.loadRequests();
    }


    loadRequests() {
        this.myRequests = [];
        this.teamRequests = [];

        this._requestService.getMyRequests().subscribe(
            data => {
                this.requests = data;
                this.myRequests = this.requests['myRequests'];
                this.teamRequests = this.requests['teamRequests'];
            },
            error => {
            }
        )
    }

    reloadData(event) {
        this.loadRequests();
    }


}
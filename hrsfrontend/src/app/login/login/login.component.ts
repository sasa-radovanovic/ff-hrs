import { Component, OnInit } from '@angular/core';
import { UserService } from "../../services/user.service";
import { Router } from "@angular/router";
import { FormControl, FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';


@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

    loginForm: FormGroup;
    wrongCredentials: boolean = false;



    constructor(private _userService: UserService, private router: Router, private fb: FormBuilder) {
    };



    ngOnInit() {
        console.log('login component');
        this.loginForm = this.fb.group({
            username: ['', [Validators.required]],
            password: ['', [Validators.required]],
        });

        this._userService.retrieveUser().subscribe(
            user => {
                if (user['role'] === 'ROLE_ADMIN') {
                    console.log('ADMIN user')
                    this.toAdminHomePage();
                } else if (user['role'] === 'ROLE_TEAM_ADMIN') {
                    console.log('TEAM ADMIN user')
                } else {
                    console.log('USER user')
                }
            },
            error => {
                console.log('should go to login');
            }
        )
    }

    onSubmit(formValue) {
        this.login(formValue['username'], formValue['password']);
    }

    toAdminHomePage() {
        this.router.navigate(['team-overview']);
    }


    toMyRequestsPage() {
        this.router.navigate(['my-requests']);
    }


    login(username, password) {
        this.wrongCredentials = false;
        this._userService.login(username, password).subscribe(
            user => {
                console.log('USER IS HERE ', user)
                this._userService.retrieveUser().subscribe(
                    userData => {
                        console.log('2 USER DATA IS HERE ', userData)
                        console.log('ROLE', userData['role'])
                        if (userData['role'] === 'ROLE_ADMIN') {
                            console.log('ADMIN user')
                            this.toAdminHomePage();
                        } else if (userData['role'] === 'ROLE_TEAM_ADMIN') {
                            this.toMyRequestsPage();
                        } else {
                            this.toMyRequestsPage();
                        }
                    },
                    error => {
                        console.log('ERROR ', error);
                        this.wrongCredentials = true;
                    }
                )
            },
            error => {
                console.log('ERROR ', error);
                this.wrongCredentials = true;
            }
        )
    }

}

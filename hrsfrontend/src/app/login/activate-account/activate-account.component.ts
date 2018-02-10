import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material";

@Component({
    selector: 'app-activate-account',
    templateUrl: './activate-account.component.html',
    styleUrls: ['./activate-account.component.css']
})
export class ActivateAccountComponent implements OnInit {


    activateAccountForm: FormGroup;
    passwordMatch: boolean;
    hashValidated: boolean;
    validating: boolean;
    user: object;

    hash: string;
    username: string;


    constructor(private _userService: UserService, private route: ActivatedRoute, private router: Router, private fb: FormBuilder,
                public snackBar: MatSnackBar) {
        this.route.queryParams
            .subscribe(params => {
                if (params !== undefined && params['hash'] !== undefined && params['username'] !== undefined) {
                    this.hash = params['hash'];
                    this.username = params['username'];
                } else {
                    router.navigate(['login']);
                }
            });
    }

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    onSubmit(value) {
        this._userService.setPassword(this.hash, this.username, value.password).subscribe(
            data => {
                this.openSnackBar("Password successfully set", "OK");
                this.router.navigate(['login']);
            },
            error => {
                this.openSnackBar("Error occured while setting password", "OK");
            }
        );
    }

    ngOnInit() {
        this.validating = true;
        this._userService.validateActivation(this.hash, this.username).subscribe(
            data => {
                this.validating = false;
                this.hashValidated = true;
                this.user = data;
            },
            error => {
                this.validating = false;
                this.hashValidated = false;
            }
        );
        this.passwordMatch = true;
        this.activateAccountForm = this.fb.group({
            password: ['', [
                    Validators.required,
                    validateOnlyAlphanumeric
                ]
            ],
            repeatPassword: ['', [
                    Validators.required,
                    validateOnlyAlphanumeric
                ]
            ]
        });
        this.activateAccountForm.valueChanges.subscribe(
            form => {
                console.log(form);
                if (form.password === form.repeatPassword) {
                    this.passwordMatch = true;
                } else {
                    this.passwordMatch = false;
                }
            }
        );
    }

}


function validateOnlyAlphanumeric(c: FormControl) {

    if (c.value === null || c.value === '') {
        return null;
    }

    return (/^([a-zA-Z0-9_-]){8,8}$/.test(c.value)) ? null : {
        validatePassword: {
            invalidPassword: false
        }
    };
}
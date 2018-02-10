import {Component, OnInit} from '@angular/core';
import {MatSnackBar} from "@angular/material";
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";

@Component({
    selector: 'app-new-user',
    templateUrl: './new-user.component.html',
    styleUrls: ['./new-user.component.css']
})
export class NewUserComponent implements OnInit {

    newUserForm: FormGroup;
    creatingUser: boolean;

    constructor(private router: Router, private fb: FormBuilder, private _userService: UserService,
                public snackBar: MatSnackBar) {
    }

    ngOnInit() {
        this.creatingUser = false;
        this.newUserForm = this.fb.group({
            firstName: ['',
                [
                    Validators.required,
                    Validators.minLength(2),
                    Validators.maxLength(20),
                    validateOnlyAlphabet
                ]
            ],
            lastName: ['',
                [
                    Validators.required,
                    Validators.minLength(2),
                    Validators.maxLength(20),
                    validateOnlyAlphabet
                ]
            ],
            email: ['',
                [
                    Validators.required,
                    Validators.email
                ],
                this.validateEmailNotTaken.bind(this)
            ],
            username: ['',
                [
                    Validators.required,
                    Validators.minLength(8),
                    Validators.maxLength(12),
                    validateUsername
                ],
                this.validateUsernameNotTaken.bind(this)
            ]
        });
    }


    onSubmit(value) {
        this.creatingUser = true;
        this._userService.updateUser(value.username, value.firstName, value.lastName, value.email).subscribe(
            data => {
                this.creatingUser = false;
                this.openSnackBar("User succesfully created", "OK");
                this.router.navigate(['/user-overview', 'edit', value.username]);
            },
            error => {
                this.creatingUser = false;
                this.openSnackBar("Error occured while creating new user", "OK");
            }
        );
    }

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    valuechange(ev) {
        console.log(this.newUserForm);
    }

    validateEmailNotTaken(control: AbstractControl) {
        return this._userService.checkEmailIsInUse(control.value).map(res => {
            if (res === null) {
                return null;
            } else {
                return {
                    emailTaken: true
                };
            }
        });
    }

    validateUsernameNotTaken(control: AbstractControl) {
        return this._userService.checkUsernameIsInUse(control.value).map(res => {
            if (res === null) {
                return null;
            } else {
                return {
                    usernameTaken: true
                };
            }
        });
    }

}


function validateOnlyAlphabet(c: FormControl) {

    if (c.value === null || c.value === '') {
        return null;
    }

    return (/^[a-zA-Z\s]*$/.test(c.value)) ? null : {
        validateName: {
            invalidName: false
        }
    };
}

function validateUsername(c: FormControl) {

    if (c.value === null || c.value === '') {
        return null;
    }

    return (/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,12}$/.test(c.value)) ? null : {
        validateUsername: {
            invalidUsername: false
        }
    };
}

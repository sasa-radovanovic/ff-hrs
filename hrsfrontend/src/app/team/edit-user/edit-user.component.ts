import {Component, Inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material";
import {RequestService} from "../../services/request.service";
import {MatSnackBar} from '@angular/material';
import {TeamService} from "../../services/team.service";
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-edit-user',
    templateUrl: './edit-user.component.html',
    styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {

    username: string = '';
    user: Object;
    dataLoaded: boolean;
    unapproved: Object[];
    approved: Object[];

    constructor(private router: Router, private route: ActivatedRoute,
                private _requestService: RequestService,
                private _userService: UserService, public dialog: MatDialog,
                public snackBar: MatSnackBar,
                private _teamService: TeamService) {
        this.route.params.subscribe(params => {
            console.log("Activated team params", params);
            if (params !== undefined && params['username'] !== undefined) {
                this.username = params['username'];
            } else {
                this.username = 'NEW USER';
            }
        });
    }

    ngOnInit() {
        this.unapproved = [];
        this.approved = [];
        this.dataLoaded = false;
        this._userService.detailedUserData(this.username).subscribe(
            data => {
                console.log('THIS IS DETAILED DATA', data);
                this.user = data;
                if (this.user['requests'] !== undefined && this.user['requests'].length > 0) {
                    this.user['requests'].forEach(r => {
                        if (!r.approvedBySuperAdmin) {
                            this.unapproved.push(r);
                        } else {
                            this.approved.push(r);
                        }
                    });
                }
                this.dataLoaded = true;
            },
            error => {

            }
        );
    }


    deleteRequest(id) {
        let dialogRef = this.dialog.open(ConfirmDialog, {
            width: '500px',
            data: {
                type: 'confirm',
                question: 'Are you sure you want to delete this request?',
                data: id
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result['ok'] !== undefined && result['ok'] === true) {
                console.log('request delete ', result['data']);
                let id = result['data']['id'];
                this._requestService.deleteRequest(id).subscribe(
                    data => {
                        this.approved = this.approved.filter(r => {
                            r['id'] !== id;
                        });
                        this.unapproved = this.unapproved.filter(r => {
                            r['id'] !== id;
                        });
                        this.openSnackBar("Request deleted", "OK");
                    },
                    error => {
                        this.openSnackBar("Error occured while deleting request", "OK");
                    }
                )
            }
        });

    }


    editMail() {
        let dialogRef = this.dialog.open(ConfirmDialog, {
            width: '500px',
            data: {
                type: 'edit',
                initValue: this.user['email'],
                placeholder: 'Email',
                service: this._userService
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result['ok'] !== undefined && result['ok'] === true) {
                let newEmail = result['data']['value'];
                this._userService.updateUser(this.username, this.user['firstName'], this.user['lastName'], newEmail).subscribe(
                    data => {
                        this.user['email'] = newEmail;
                        this.openSnackBar("Email updated", "OK");
                    },
                    error => {
                        this.openSnackBar("Error occured while updating email", "OK");
                    }
                );
            }
        });
    }


    changeName() {
        let dialogRef = this.dialog.open(ConfirmDialog, {
            width: '500px',
            data: {
                type: 'multi-edit',
                name: 'Change name',
                values: [this.user['firstName'], this.user['lastName']],
                placeholders: ['First name', 'Last name']
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result['ok'] !== undefined && result['ok'] === true) {
                let fName = result['data']['firstName'];
                let lName = result['data']['lastName'];
                this._userService.updateUser(this.username, fName, lName, this.user['email']).subscribe(
                    data => {
                        this.user['firstName'] = fName;
                        this.user['lastName'] = lName;
                        this.openSnackBar("Name updated", "OK");
                    },
                    error => {
                        this.openSnackBar("Error occured while updating name", "OK");
                    }
                );
            }
        });
    }


    removeAccount() {
        let dialogRef = this.dialog.open(ConfirmDialog, {
            width: '500px',
            data: {
                type: 'confirm',
                question: 'Are you sure you want to delete this account?',
                data: ''
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result['ok'] !== undefined && result['ok'] === true) {
                this._userService.deleteUser(this.username).subscribe(
                    data => {
                        this.openSnackBar("Account deleted", "OK");
                        this.router.navigate(['user-overview']);
                    },
                    error => {
                        this.openSnackBar("Error occured while deleting account", "OK");
                    }
                )
            }
        });
    }


    approveRequest(id) {
        let dialogRef = this.dialog.open(ConfirmDialog, {
            width: '500px',
            data: {
                type: 'confirm',
                question: 'Are you sure you want to approve this request?',
                data: id
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result['ok'] !== undefined && result['ok'] === true) {
                console.log('request approve ', result['data']);
                let id = result['data']['id'];
                this._requestService.approveRequestByAdmin(id).subscribe(
                    data => {
                        let req = this.unapproved.filter(r => {
                            return r['id'] === id;
                        });
                        this.unapproved = this.unapproved.filter(r => {
                            return r['id'] !== id;
                        })
                        this.approved.push(req[0]);
                        this.openSnackBar("Request approved", "OK");
                    },
                    error => {
                        this.openSnackBar("Error occured while approving request", "OK");
                    }
                )
            }
        });
    }

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }


    removeAsTeamAdmin(team) {
        this._teamService.removeTeamAdminFromTeam(team, this.username).subscribe(
            data => {
                this.user['adminTeams'] = this.user['adminTeams'].filter(t => {
                    return t !== team;
                });
                this.openSnackBar("Team admin successfully removed from team", "OK");
            },
            error => {
                this.openSnackBar("Error occured while removing team admin from the team", "OK");
            }
        );

    }

    removeFromTeam() {
        this._teamService.removeUserFromTeam(this.user['team'], this.username).subscribe(
            data => {
                this.user['team'] = '';
                this.openSnackBar("User successfully removed from team", "OK");
            },
            error => {
                this.openSnackBar("Error occured while removing user from the team", "OK");
            }
        );
    }

}


@Component({
    selector: 'confirm-dialog',
    templateUrl: 'confirm-dialog.html',
    styleUrls: ['./edit-user.component.css']
})
export class ConfirmDialog {

    textValue: string = '';
    firstName: string = '';
    lastName: string = '';
    _userService: UserService;

    editForm: FormGroup;
    editNameForm: FormGroup;

    constructor(public dialogRef: MatDialogRef<ConfirmDialog>,
                @Inject(MAT_DIALOG_DATA) public data: any, private fb: FormBuilder) {
        if (data['type'] === 'edit' && data['initValue'] !== undefined) {
            this.textValue = data['initValue'];
            this._userService = this.data['service'];
            this.editForm = this.fb.group({
                email: ['',
                    [
                        Validators.required,
                        Validators.email
                    ],
                    this.validateEmailNotTaken.bind(this)
                ]
            });
            this.editForm
                .setValue({
                    email: this.textValue
                })
        } else if (data['type'] === 'multi-edit') {
            this.editNameForm = this.fb.group({
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
                ]
            });
            this.editNameForm
                .setValue({
                    firstName: this.data['values'][0],
                    lastName: this.data['values'][1]
                });
        }
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


    onFormMultiEditYes(value) {
        this.dialogRef.close({
            ok: true,
            data: {
                firstName: value.firstName,
                lastName: value.lastName
            }
        });
    }

    onFormYes(value) {
        this.dialogRef.close({
            ok: true,
            data: {
                value: value.email
            }
        });
    }

    onNoClick(): void {
        this.dialogRef.close({
            ok: false,
            data: {}
        });
    }

    onYesClick(): void {
        this.dialogRef.close({
            ok: true,
            data: {
                id: this.data['data']
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
import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {TeamService} from "../../services/team.service";
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material";

@Component({
    selector: 'app-new-team',
    templateUrl: './new-team.component.html',
    styleUrls: ['./new-team.component.css']
})
export class NewTeamComponent implements OnInit {

    newTeamForm: FormGroup;

    constructor(private router: Router, private fb: FormBuilder, private _teamService: TeamService,
                public snackBar: MatSnackBar) {
    }

    ngOnInit() {
        this.newTeamForm = this.fb.group({
            teamName: ['',
                [
                    Validators.required,
                    Validators.minLength(4),
                    validateName
                ],
                this.validateNameNotTaken.bind(this)
            ],
        });
    }


    onSubmit(formValue) {
        console.log(formValue['teamName']);
        this._teamService.createTeam(formValue['teamName']).subscribe(
            data => {
                this.openSnackBar("Team created successfully", "OK");
                this.router.navigate(['/team-overview']);
            },
            error => {
                this.openSnackBar("Error creating new team", "OK");
            }
        );
    }

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    validateNameNotTaken(control: AbstractControl) {
        return this._teamService.checkNameTaken(control.value).map(res => {
            if (res === null) {
                return null;
            } else {
                return {
                    nameTaken: true
                };
            }
        });
    }
}

function validateName(c: FormControl) {

    if (c.value === null || c.value === '') {
        return null;
    }

    return (/^[a-z0-9]+$/i.test(c.value)) ? null : {
        validateName: {
            invalidName: false
        }
    };
}
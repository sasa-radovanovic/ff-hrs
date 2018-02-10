import {Component, OnInit} from '@angular/core';
import {RequestService} from "../../services/request.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatDatepickerInputEvent, MatSnackBar} from "@angular/material";

import * as moment from "moment";
import {Router} from "@angular/router";

@Component({
    selector: 'app-holiday-bid',
    templateUrl: './holiday-bid.component.html',
    styleUrls: ['./holiday-bid.component.css']
})
export class HolidayBidComponent implements OnInit {

    newHolidayRequestForm: FormGroup;
    minDateStart = new Date();
    maxDateStart = new Date();
    minDateEnd = new Date();
    maxDateEnd = new Date();
    startingDate = new Date();
    endingDate = new Date();
    datesValidated: boolean;
    requestLength: number;
    validating: boolean = false;

    constructor(private _requestService: RequestService, private fb: FormBuilder, public snackBar: MatSnackBar, private router: Router) {
        this.maxDateStart.setFullYear(new Date().getFullYear() + 1)
        this.maxDateEnd.setFullYear(new Date().getFullYear() + 1)
        this.endingDate.setDate(this.startingDate.getDate() + 7)
        this.requestLength = 7
    }


    ngOnInit() {
        this.newHolidayRequestForm = this.fb.group({
            startDate: [this.startingDate,
                [
                    Validators.required
                ]
            ],
            endDate: [this.endingDate,
                [
                    Validators.required
                ]
            ]
        })
        this.validateDates();
    }


    startDateChange(event: MatDatepickerInputEvent<Date>) {
        this.minDateEnd = new Date(this.addDays(event.value, 1))
        if (this.newHolidayRequestForm.value.endDate <= event.value) {
            let newValue = new Date(this.addDays(event.value, 1))
            this.newHolidayRequestForm.setValue({
                startDate: event.value,
                endDate: newValue
            });
        }
        this.validateDates();
    }

    endDateChange(event: MatDatepickerInputEvent<Date>) {
        console.log('end date change ', event.value)
        this.recalculateDuration();
        this.validateDates();
    }


    validateDates() {
        this.recalculateDuration();
        this.validating = true;
        this._requestService.validateDates(
            moment(this.newHolidayRequestForm.value.startDate).format('DD/MM/YYYY'),
            moment(this.newHolidayRequestForm.value.endDate).format('DD/MM/YYYY')
        ).subscribe(
            data => {
                this.validating = false;
                this.datesValidated = data['validDates']
            }
        )
    }


    recalculateDuration() {
        let dateE = moment(this.newHolidayRequestForm.value.endDate);
        let dateS = moment(this.newHolidayRequestForm.value.startDate);
        let duration = moment.duration(dateE.diff(dateS));
        this.requestLength = Math.ceil(duration.asDays()) + 1;
    }


    createNewHolidayRequest(data) {
        this._requestService.newRequest(
            moment(this.newHolidayRequestForm.value.startDate).format('DD/MM/YYYY'),
            moment(this.newHolidayRequestForm.value.endDate).format('DD/MM/YYYY')
        ).subscribe(
            data => {
                this.openSnackBar("New holiday request created", "OK")
                this.router.navigate(["my-requests"]);
            }
        )

    }


    addDays(date, days) {
        let result = new Date(date);
        result.setDate(result.getDate() + days);
        return result;
    }


    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }


}

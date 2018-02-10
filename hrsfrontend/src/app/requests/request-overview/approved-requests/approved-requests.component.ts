import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {MatPaginator, MatSnackBar, MatTableDataSource} from "@angular/material";
import {RequestService} from "../../../services/request.service";

@Component({
    selector: 'approved-requests',
    templateUrl: './approved-requests.component.html',
    styleUrls: ['./approved-requests.component.css']
})
export class ApprovedRequestsComponent implements OnInit, OnChanges {

    totalRequests: number;
    requests: Object[];
    dataSource;

    @Input() reloadData: number;
    changes: number = 0;
    @Output() dataChanged = new EventEmitter<number>();

    @ViewChild(MatPaginator) paginator: MatPaginator;


    constructor(private _requestService: RequestService, public snackBar: MatSnackBar) {
        this.requests = [];
        this.totalRequests = 0;
        console.log('Approved constructor');
    }

    ngOnInit() {
        this.loadRequests();
    }

    ngOnChanges(changes: SimpleChanges) {
        console.log('APPROVED ', changes.reloadData.currentValue)
        if (changes.reloadData.currentValue > 0) {
            this.loadRequests();
        }

    }

    removeRequest(id) {
        this._requestService.deleteRequest(id).subscribe(
            data => {
                this.openSnackBar("Request deleted", "OK");
                this.changes ++;
                this.dataChanged.emit(this.changes);
            },
            error => {
            }
        );
    }


    loadRequests () {
        this._requestService.allApprovedRequests().subscribe(
            data => {
                this.totalRequests = data.length;
                this.requests = data;
                this.dataSource = new MatTableDataSource(this.requests);
                this.dataSource.paginator = this.paginator;
            },
            error => {
            }
        )
    }

    applyFilter(filterValue: string) {
        console.log('Filter ' + filterValue);
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

}

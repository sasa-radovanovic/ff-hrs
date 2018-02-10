import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {RequestService} from "../../../services/request.service";
import {MatDialog, MatPaginator, MatSnackBar, MatTableDataSource} from "@angular/material";
import {Router} from "@angular/router";

@Component({
    selector: 'pending-requests',
    templateUrl: './pending-requests.component.html',
    styleUrls: ['./pending-requests.component.css']
})
export class PendingRequestsComponent implements OnInit, OnChanges {


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
    }

    ngOnInit() {
        this.loadRequests();
    }

    ngOnChanges(changes: SimpleChanges) {
        console.log('PENDING ', changes.reloadData.currentValue)
        if (changes.reloadData.currentValue > 0) {
            this.loadRequests();
        }
    }

    loadRequests() {
        this._requestService.allPendingRequests().subscribe(
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

    approveRequest(id) {
        this._requestService.approveRequestByAdmin(id).subscribe(
            data => {
                this.openSnackBar("Request approved", "OK");
                this.changes ++;
                this.dataChanged.emit(this.changes);
            },
            error => {
            }
        );
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

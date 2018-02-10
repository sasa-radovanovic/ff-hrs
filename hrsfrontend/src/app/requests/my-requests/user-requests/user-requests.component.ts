import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {MatPaginator, MatSnackBar, MatTableDataSource} from "@angular/material";
import {RequestService} from "../../../services/request.service";

@Component({
    selector: 'user-requests',
    templateUrl: './user-requests.component.html',
    styleUrls: ['./user-requests.component.css']
})
export class UserRequestsComponent implements OnInit, OnChanges {

    @Input() userRequests;
    dataSource;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    @Output() dataChanged = new EventEmitter<boolean>();

    constructor(private _requestService: RequestService, public snackBar: MatSnackBar) {
    }

    ngOnInit() {
        this.dataSource = new MatTableDataSource(this.userRequests);
        this.dataSource.paginator = this.paginator;
    }

    ngOnChanges(changes: SimpleChanges) {
        this.dataSource = new MatTableDataSource(this.userRequests);
        this.dataSource.paginator = this.paginator;
    }


    removeRequest(id) {
        this._requestService.deleteUserRequest(id).subscribe(data => {
            this.openSnackBar("Request deleted", "OK");
            this.dataChanged.emit(true)
        })
    }

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }
}

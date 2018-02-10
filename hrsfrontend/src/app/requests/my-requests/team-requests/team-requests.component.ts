import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {MatPaginator, MatTableDataSource} from "@angular/material";

@Component({
    selector: 'team-requests',
    templateUrl: './team-requests.component.html',
    styleUrls: ['./team-requests.component.css']
})
export class TeamRequestsComponent implements OnInit, OnChanges {


    @Input() teamRequests;
    dataSource;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor() {
    }

    ngOnInit() {
        this.dataSource = new MatTableDataSource(this.teamRequests);
        this.dataSource.paginator = this.paginator;
    }

    ngOnChanges(changes: SimpleChanges) {
        this.dataSource = new MatTableDataSource(this.teamRequests);
        this.dataSource.paginator = this.paginator;
    }


    applyFilter(val) {

    }
}

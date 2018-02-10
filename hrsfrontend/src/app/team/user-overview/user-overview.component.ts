import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";
import {MatPaginator, MatTableDataSource} from "@angular/material";

@Component({
    selector: 'app-user-overview',
    templateUrl: './user-overview.component.html',
    styleUrls: ['./user-overview.component.css']
})
export class UserOverviewComponent implements OnInit {

    dataLoaded: boolean = false;
    dataSource;
    users: Object[];
    usedCriteria: string;


    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(private router: Router, private _userService: UserService) {
        this.users = [];
        this.dataLoaded = false;
        this.usedCriteria = '';
    }

    ngOnInit() {
    }

    applyFilter(criteria: string) {
        this.dataLoaded = false;
        if (criteria.length > 2) {
            this.usedCriteria = criteria;
            this._userService.partialSearch(criteria).subscribe(
                data => {
                    console.log('partial search data ', data)
                    this.users = data;
                    this.dataSource = new MatTableDataSource(data);
                    this.dataSource.paginator = this.paginator;
                    this.dataLoaded = true;
                },
                error => {

                }
            );
        }
    }
}

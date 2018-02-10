import {Component, OnInit, ViewChild} from '@angular/core';
import {TeamService} from "../../services/team.service";
import {Router} from "@angular/router";
import {MatDialog, MatPaginator, MatSnackBar, MatTableDataSource} from "@angular/material";
import {ConfirmDialog} from "../edit-user/edit-user.component";


@Component({
    selector: 'app-team-overview',
    templateUrl: './team-overview.component.html',
    styleUrls: ['./team-overview.component.css']
})
export class TeamOverviewComponent implements OnInit {


    totalTeams: number;
    teams: Object[];
    available: boolean;
    dataSource;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(private _teamService: TeamService, private _router: Router, public dialog: MatDialog,
                public snackBar: MatSnackBar) {
        this.teams = [];
        this.available = false;
        this.totalTeams = 0;
    }

    ngOnInit() {
        this.loadTeams();
    }


    loadTeams() {
        this._teamService.getAllTeams(-1).subscribe(
            teamData => {
                console.log('Retrived team data in team overview', teamData);
                this.totalTeams = teamData['totalTeams'];
                console.log('concat teams ', teamData['teams']);
                this.teams = this.teams.concat(teamData['teams']);
                this.available = teamData['available'];
                this.dataSource = new MatTableDataSource(this.teams);
                console.log('DATA SOURCE', this.dataSource);
                this.dataSource.paginator = this.paginator;
            },
            error => {
                console.log('should go to login');
            }
        )
    }


    toTeamDetails(name) {
        alert('TEAM DETAILS')
        //this._router.navigate(['team-overview', 'team-details', {name: name}]);
        this._router.navigate(['user-overview']);

    }

    deleteTeam(name) {
        let dialogRef = this.dialog.open(ConfirmDialog, {
            width: '500px',
            data: {
                type: 'confirm',
                question: 'Are you sure you want to delete this team?',
                data: ''
            }
        });


        dialogRef.afterClosed().subscribe(result => {
            if (result['ok'] !== undefined && result['ok'] === true) {
                let teamName = name;
                this._teamService.deleteTeam(name).subscribe(
                    data => {
                        this.totalTeams --;
                        this.teams = this.teams.filter(t => {
                            return t['name'] !== teamName;
                        });

                        this.dataSource = new MatTableDataSource(this.teams);
                        this.dataSource.paginator = this.paginator;
                        console.log(this.dataSource);
                        this.openSnackBar("Team successfully deleted", "OK");
                    },
                    error => {
                        this.openSnackBar("Error occured while deleting team", "OK");
                    }
                );
            }
        });
    }

    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    applyFilter(filterValue: string) {
        console.log('Filter ' + filterValue);
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }


}

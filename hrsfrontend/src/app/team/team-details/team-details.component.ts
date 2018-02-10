import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {TeamService} from "../../services/team.service";
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatTableDataSource, MatSnackBar} from '@angular/material';


@Component({
    selector: 'app-team-details',
    templateUrl: './team-details.component.html',
    styleUrls: ['./team-details.component.css']
})
export class TeamDetailsComponent implements OnInit {

    teamDetails: any;
    teamName: string = '';
    detailsLoaded: boolean = false;
    totalRequests: number = 0;
    totalPending: number = 0;

    constructor(private router: Router, private route: ActivatedRoute, private _teamService: TeamService, public dialog: MatDialog,
                public snackBar: MatSnackBar) {
        this.route.params.subscribe(params => {
            console.log("Activated team params", params);
            if (params !== undefined && params['name'] !== undefined) {
                this.teamName = params['name'];
            } else {
                router.navigate(['team-overview']);
            }
        });
    }

    ngOnInit() {
        this.initializeData();
    }


    openSnackBar(message: string, action: string) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }


    initializeData() {
        this.totalRequests = 0;
        this.totalPending = 0;
        this.detailsLoaded = false;
        this._teamService.getTeamDetails(this.teamName).subscribe(
            teamData => {
                this.teamDetails = teamData;
                this.attachNumOfUnapprovedRequests();
                teamData['users'].map(u => {
                    if (u['requests'] !== undefined && u['requests'].length > 0) {
                        return u['requests'].length;
                    } else {
                        return 0;
                    }
                }).forEach(r => {
                    this.totalRequests += r;
                })
                this.detailsLoaded = true;
            },
            error => {
                this.router.navigate(['team-overview']);
            }
        );
    }

    private attachNumOfUnapprovedRequests() {
        let allUsers = this.teamDetails['users'];
        if (allUsers === undefined || allUsers.length === 0) {
            return;
        }
        this.teamDetails['users'].forEach(u => {
            let allRequests = u['requests'];
            if (allRequests !== undefined && allRequests.length !== 0) {
                let totalUnapproved = allRequests.filter(r => {
                    return !r['approvedByTeamAdmin'] || !r['approvedBySuperAdmin']
                }).length;
                u['totalUnapproved'] = totalUnapproved;
                this.totalPending ++;
            } else {
                u['totalUnapproved'] = 0;
            }
        });
    }

    userClicked() {
        alert('user clicked');
    }

    newUser() {
        this.openDialog('user');
    }


    newTeamAdmin() {
        this.openDialog('team_admin');
    }

    openDialog(type): void {
        let dialogRef = this.dialog.open(NewUserDialog, {
            width: '800px',
            data: {
                type: type,
                team: this.teamName,
                service: this._teamService
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result['reload'] !== undefined && result['reload'] === true) {
                this.openSnackBar("User succesfully added", "OK");
                this.initializeData();
            }
        });
    }

}


@Component({
    selector: 'new-user-dialog',
    templateUrl: 'new-user-dialog.html',
    styleUrls: ['./team-details.component.css']
})
export class NewUserDialog {

    dataSource;
    dataLoaded: boolean = false;
    dataLoading: boolean = false;
    searchResult: any[];
    usedCriteria: string = '';
    private _teamService: TeamService;

    constructor(
        public dialogRef: MatDialogRef<NewUserDialog>,
        @Inject(MAT_DIALOG_DATA) public data: any) {
        this.searchResult = [];
        this._teamService = data['service'];
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    addUser(username): void {
        this._teamService.addUserToTeam(username, this.data['team']).subscribe(
            data => {
                this.dialogRef.close({
                    reload: true
                });
            },
            error => {
                this.dialogRef.close({
                    reload: false
                });
            }
        );
    }

    addTeamAdmin(username): void {
        this._teamService.addTeamAdminToTeam(username, this.data['team']).subscribe(
            data => {
                this.dialogRef.close({
                    reload: true
                });
            },
            error => {
                this.dialogRef.close({
                    reload: false
                });
            }
        );
    }


    searchUsers(value): void {
        if (value !== undefined && value.length > 2) {
            this.dataLoading = true;
            this._teamService.newTeamMemberPartialSearch(value, this.data['team']).subscribe(
                data => {
                    this.usedCriteria = value;
                    this.dataLoading = false;
                    this.dataLoaded = true;
                    this.searchResult = data;
                    this.dataSource = new MatTableDataSource(data);
                },
                error => {
                    this.usedCriteria = value;
                    this.dataLoading = false;
                    this.searchResult = [];
                    this.dataLoaded = true;
                    this.dataSource = new MatTableDataSource([]);
                }
            );
        }
    }
}
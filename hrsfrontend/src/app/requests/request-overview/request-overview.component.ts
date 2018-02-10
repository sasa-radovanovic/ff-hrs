import { Component, OnInit } from '@angular/core';
import {RequestService} from "../../services/request.service";
import {MatSnackBar} from "@angular/material";
import {Router} from "@angular/router";

@Component({
  selector: 'app-request-overview',
  templateUrl: './request-overview.component.html',
  styleUrls: ['./request-overview.component.css']
})
export class RequestOverviewComponent implements OnInit {



  changes: number = 0;

  constructor(private _requestService: RequestService, private router: Router,
              public snackBar: MatSnackBar) { }

  ngOnInit() {
  }

  handleChange(data) {
    this.changes = data;
  }


}

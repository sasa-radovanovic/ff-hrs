import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovedRequestsComponent } from './approved-requests.component';

describe('ApprovedRequestsComponent', () => {
  let component: ApprovedRequestsComponent;
  let fixture: ComponentFixture<ApprovedRequestsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApprovedRequestsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApprovedRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecruteurDashboardComponent } from './recruteur-dashboard.component';

describe('RecruteurDashboardComponent', () => {
  let component: RecruteurDashboardComponent;
  let fixture: ComponentFixture<RecruteurDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecruteurDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecruteurDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

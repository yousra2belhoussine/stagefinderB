import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterStagiaireComponent } from './register-stagiaire.component';

describe('RegisterStagiaireComponent', () => {
  let component: RegisterStagiaireComponent;
  let fixture: ComponentFixture<RegisterStagiaireComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterStagiaireComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterStagiaireComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

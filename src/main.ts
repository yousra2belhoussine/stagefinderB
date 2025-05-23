//import { bootstrapApplication } from '@angular/platform-browser';
//import { appConfig } from './app/app.config';
//import { AppComponent } from './app/app.component';

//bootstrapApplication(AppComponent, appConfig)
  //.catch(err => console.error(err));
  import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes'; // 👉 assure-toi que le chemin est correct
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, {
  ...appConfig,
  providers: [
    ...appConfig.providers || [], // conserve les autres providers de appConfig
    provideRouter(routes)        // 👉 active le router
  ]
}).catch(err => console.error(err));

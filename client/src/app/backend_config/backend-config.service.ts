import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BackendConfigService {

  // backend_url ='http://localhost:9000/webapi';
  backend_url = 'http://Emartapp-ALB-1488438150.us-east-1.elb.amazonaws.com/webapi';

  constructor() { }
}

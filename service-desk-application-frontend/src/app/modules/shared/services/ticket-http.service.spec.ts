import { TestBed } from '@angular/core/testing';

import { TicketHttpService } from './ticket-http.service';

describe('TicketHttpService', () => {
  let service: TicketHttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketHttpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

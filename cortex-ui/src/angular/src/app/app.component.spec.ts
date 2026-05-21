import { describe, it, expect } from 'vitest';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  it('should create', () => {
    const component = new AppComponent();
    expect(component).toBeTruthy();
  });
});

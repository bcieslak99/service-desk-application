import { Injectable } from '@angular/core';
import { NativeDateAdapter } from '@angular/material/core';

@Injectable()
export class CustomDateAdapter extends NativeDateAdapter {
  override getMonthNames(style: 'long' | 'short' | 'narrow'): string[] {
    switch (style) {
      case 'long':
        return [
          'styczeń', 'luty', 'marzec', 'kwiecień', 'maj', 'czerwiec',
          'lipiec', 'sierpień', 'wrzesień', 'październik', 'listopad', 'grudzień'
        ];
      case 'short':
        return [
          'sty', 'lut', 'mar', 'kwi', 'maj', 'cze',
          'lip', 'sie', 'wrz', 'paź', 'lis', 'gru'
        ];
      case 'narrow':
        return [
          'S', 'L', 'M', 'K', 'M', 'C',
          'L', 'S', 'W', 'P', 'L', 'G'
        ];
    }
  }

  override format(date: Date, displayFormat: any): string {
    const day = this._to2digit(date.getDate());
    const month = this._to2digit(date.getMonth() + 1);
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }

  private _to2digit(n: number): string {
    return ('00' + n).slice(-2);
  }
}

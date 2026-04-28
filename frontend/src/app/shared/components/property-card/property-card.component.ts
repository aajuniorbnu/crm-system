import { CurrencyPipe, DecimalPipe, NgOptimizedImage } from '@angular/common';
import { Component, computed, input, signal } from '@angular/core';
import {
  calculateValuePerSquareMeter,
  formatEnumLabel,
  Imovel
} from '../../../core/models/imovel.model';

@Component({
  selector: 'app-property-card',
  imports: [CurrencyPipe, DecimalPipe, NgOptimizedImage],
  templateUrl: './property-card.component.html'
})
export class PropertyCardComponent {
  readonly imovel = input.required<Imovel>();
  readonly expanded = signal(false);

  readonly valorMetroQuadrado = computed(() =>
    calculateValuePerSquareMeter(this.imovel().areaM2, this.imovel().valor)
  );

  readonly imageUrl = computed(
    () =>
      this.imovel().imagemUrl?.trim() ||
      'https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=1200&q=80'
  );

  toggleDetails(): void {
    this.expanded.update((value) => !value);
  }

  protected readonly formatEnumLabel = formatEnumLabel;
}

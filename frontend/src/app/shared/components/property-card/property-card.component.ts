import { CurrencyPipe, DecimalPipe, NgClass } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import {
  calculateValuePerSquareMeter,
  formatEnumLabel,
  Imovel
} from '../../../core/models/imovel.model';

@Component({
  selector: 'app-property-card',
  imports: [CurrencyPipe, DecimalPipe, NgClass],
  templateUrl: './property-card.component.html',
  styleUrl: './property-card.component.scss'
})
export class PropertyCardComponent {
  readonly imovel = input.required<Imovel>();

  readonly toneClass = computed(() =>
    this.imovel().finalidade === 'VENDA' ? 'property-card--sale' : 'property-card--rent'
  );

  readonly categoriaCode = computed(() => {
    const codes: Record<Imovel['categoria'], string> = {
      APARTAMENTO: 'APT',
      CASA: 'CAS',
      COBERTURA: 'COB',
      TERRENO: 'TRN',
      COMERCIAL: 'COM',
      RURAL: 'RUR'
    };

    return codes[this.imovel().categoria];
  });

  readonly finalidadeLabel = computed(() =>
    this.imovel().finalidade === 'VENDA' ? 'A venda' : 'Para aluguel'
  );

  readonly valorMetroQuadrado = computed(() =>
    calculateValuePerSquareMeter(this.imovel().areaM2, this.imovel().valor)
  );

  protected readonly formatEnumLabel = formatEnumLabel;
}

import { Pipe, PipeTransform } from '@angular/core';
import { Livraison, LivraisonStatus } from '../models/livraison.model';

@Pipe({
  name: 'filterByStatut'
})
export class FilterByStatutPipe implements PipeTransform {
  transform(livraisons: Livraison[], statut: LivraisonStatus): number {
    return livraisons.filter(l => l.statut === statut).length;
  }
}
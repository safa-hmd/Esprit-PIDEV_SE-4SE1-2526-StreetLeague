import { Component, OnInit } from '@angular/core';
import { MaterielService } from '../../../services/Materiel.service';
import { Materiel, Category } from '../../../models/materiel.model';

@Component({
  selector: 'app-materiel-list',
  templateUrl: './materiel-list.component.html',
  styleUrls: ['./materiel-list.component.css']
})
export class MaterielListComponent implements OnInit {

  // ── Données ──────────────────────────────────────────
  materiels: Materiel[] = [];
  filteredMateriels: Materiel[] = [];
  categories: Category[] = [];

  // ── Recherche / filtre ───────────────────────────────
  searchTerm = '';
  selectedCatFilter: number | null = null;

  // ── Formulaire (create + edit) ───────────────────────
  showForm = false;
  editMode = false;
  editId: number | null = null;

  form: Materiel = this.emptyForm();

  // ── Upload image ─────────────────────────────────────
  imageSource: 'upload' | 'url' = 'upload';
  uploadingImage = false;
  isDragOver = false;

  // ── État UI ──────────────────────────────────────────
  loading = false;
  successMsg = '';
  errorMsg = '';

  // ── Vue active (liste | categories) ─────────────────
  activeTab: 'materiels' | 'categories' = 'materiels';

  // ── CRUD Catégories (inline) ─────────────────────────
  showCatForm = false;
  editCatMode = false;
  editCatId: number | null = null;
  catForm: Category = { nom: '', description: '' };

  constructor(private materielService: MaterielService) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadMateriels();
  }

  // ── Chargement ───────────────────────────────────────
  loadMateriels(): void {
    this.loading = true;
    this.materielService.getAll().subscribe({
      next: data => {
        this.materiels = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => { this.showError('Erreur chargement matériels.'); this.loading = false; }
    });
  }

  loadCategories(): void {
    this.materielService.getAllCategories().subscribe({
      next: data => this.categories = data,
      error: () => {}
    });
  }

  // ── Recherche / Filtre ───────────────────────────────
  applyFilters(): void {
    let result = [...this.materiels];
    if (this.searchTerm.trim()) {
      const t = this.searchTerm.toLowerCase();
      result = result.filter(m =>
        m.nom.toLowerCase().includes(t) ||
        m.description.toLowerCase().includes(t) ||
        m.categorieNom?.toLowerCase().includes(t)
      );
    }
    if (this.selectedCatFilter !== null) {
      result = result.filter(m => m.categorieId === this.selectedCatFilter);
    }
    this.filteredMateriels = result;
  }

  // ── Formulaire matériel ──────────────────────────────
  openCreate(): void {
    this.editMode = false;
    this.editId = null;
    this.form = this.emptyForm();
    this.imageSource = 'upload';
    this.showForm = true;
  }

  openEdit(m: Materiel): void {
    this.editMode = true;
    this.editId = m.id!;
    this.form = {
      nom: m.nom,
      description: m.description,
      prix: m.prix,
      quantiteStock: m.quantiteStock,
      imageUrl: m.imageUrl || '',
      categorieId: m.categorieId,
    };
    // Si l'image existante ressemble à une URL externe, ouvrir en mode URL
    this.imageSource = (m.imageUrl && m.imageUrl.startsWith('http')) ? 'url' : 'upload';
    this.showForm = true;
    setTimeout(() => document.getElementById('materiel-form')?.scrollIntoView({ behavior: 'smooth' }), 100);
  }

  submitForm(): void {
    if (!this.form.nom?.trim() || !this.form.description?.trim() || !this.form.categorieId) {
      this.showError('Nom, description et catégorie sont obligatoires.');
      return;
    }
    if (this.form.prix <= 0) {
      this.showError('Le prix doit être supérieur à 0.');
      return;
    }

    const action$ = this.editMode && this.editId
      ? this.materielService.update(this.editId, this.form)
      : this.materielService.create(this.form);

    action$.subscribe({
      next: () => {
        this.showSuccess(this.editMode ? 'Matériel mis à jour !' : 'Matériel créé !');
        this.closeForm();
        this.loadMateriels();
      },
      error: () => this.showError('Erreur lors de l\'enregistrement.')
    });
  }

  delete(m: Materiel): void {
    if (!confirm(`Supprimer "${m.nom}" ?`)) return;
    this.materielService.delete(m.id!).subscribe({
      next: () => { this.showSuccess('Matériel supprimé.'); this.loadMateriels(); },
      error: () => this.showError('Erreur lors de la suppression.')
    });
  }

  closeForm(): void {
    this.showForm = false;
    this.form = this.emptyForm();
    this.uploadingImage = false;
    this.isDragOver = false;
  }

  // ── Upload image depuis PC ───────────────────────────

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.uploadFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
    const file = event.dataTransfer?.files[0];
    if (file && file.type.startsWith('image/')) {
      this.uploadFile(file);
    } else {
      this.showError('Veuillez déposer un fichier image valide.');
    }
  }

  private uploadFile(file: File): void {
    if (file.size > 5 * 1024 * 1024) {
      this.showError('L\'image ne doit pas dépasser 5 MB.');
      return;
    }
    if (!file.type.startsWith('image/')) {
      this.showError('Format non supporté. Utilisez JPG, PNG ou WEBP.');
      return;
    }

    this.uploadingImage = true;
    this.materielService.uploadImage(file).subscribe({
      next: (response) => {
        this.form.imageUrl = response.imageUrl;
        this.uploadingImage = false;
      },
      error: () => {
        this.showError('Erreur lors de l\'upload de l\'image.');
        this.uploadingImage = false;
      }
    });
  }

  clearImage(event: Event): void {
    event.stopPropagation();
    this.form.imageUrl = '';
  }

  // ── CRUD Catégories ──────────────────────────────────
  openCreateCat(): void {
    this.editCatMode = false;
    this.editCatId = null;
    this.catForm = { nom: '', description: '' };
    this.showCatForm = true;
  }

  openEditCat(c: Category): void {
    this.editCatMode = true;
    this.editCatId = c.id!;
    this.catForm = { nom: c.nom, description: c.description || '' };
    this.showCatForm = true;
  }

  submitCatForm(): void {
    if (!this.catForm.nom?.trim()) {
      this.showError('Le nom de la catégorie est obligatoire.');
      return;
    }
    const action$ = this.editCatMode && this.editCatId
      ? this.materielService.updateCategory(this.editCatId, this.catForm)
      : this.materielService.createCategory(this.catForm);

    action$.subscribe({
      next: () => {
        this.showSuccess(this.editCatMode ? 'Catégorie mise à jour !' : 'Catégorie créée !');
        this.closeCatForm();
        this.loadCategories();
      },
      error: () => this.showError('Erreur lors de l\'enregistrement de la catégorie.')
    });
  }

  deleteCat(c: Category): void {
    if (!confirm(`Supprimer la catégorie "${c.nom}" ? (Impossible si elle contient des matériels)`)) return;
    this.materielService.deleteCategory(c.id!).subscribe({
      next: () => { this.showSuccess('Catégorie supprimée.'); this.loadCategories(); },
      error: (err) => this.showError(err?.error?.message || 'Erreur : vérifiez que la catégorie est vide.')
    });
  }

  closeCatForm(): void {
    this.showCatForm = false;
    this.catForm = { nom: '', description: '' };
  }

  // ── Stats ────────────────────────────────────────────
  get stats() {
    const totalStock = this.materiels.reduce((s, m) => s + m.quantiteStock, 0);
    const rupture = this.materiels.filter(m => m.quantiteStock === 0).length;
    const prixMoyen = this.materiels.length
      ? this.materiels.reduce((s, m) => s + m.prix, 0) / this.materiels.length
      : 0;
    return {
      total: this.materiels.length,
      totalStock,
      rupture,
      prixMoyen: prixMoyen.toFixed(2)
    };
  }

  // ── Helpers ──────────────────────────────────────────
  getCatNom(id: number): string {
    return this.categories.find(c => c.id === id)?.nom || '—';
  }

  getStockBadge(q: number): string {
    if (q === 0) return 'badge-rupture';
    if (q <= 5) return 'badge-low';
    return 'badge-ok';
  }

  getStockLabel(q: number): string {
    if (q === 0) return 'Rupture';
    if (q <= 5) return `Limité (${q})`;
    return `${q} unités`;
  }

  private emptyForm(): Materiel {
    return { nom: '', description: '', prix: 0, quantiteStock: 0, imageUrl: '', categorieId: 0 };
  }

  private showSuccess(msg: string): void {
    this.successMsg = msg; this.errorMsg = '';
    setTimeout(() => this.successMsg = '', 3500);
  }

  private showError(msg: string): void {
    this.errorMsg = msg; this.successMsg = '';
    setTimeout(() => this.errorMsg = '', 4000);
  }
}
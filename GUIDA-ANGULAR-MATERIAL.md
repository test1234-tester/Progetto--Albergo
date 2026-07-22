# Angular Material nel progetto — mini guida pratica

Questa versione usa Angular Material come **strumento**, non come identità grafica preconfezionata. Material gestisce comportamento/accessibilità dei componenti; SCSS e CSS variables danno invece al sito l'aspetto da hotel.

## 1. Dove è installato

In `frontend/package.json` trovi:

```json
"@angular/material": "^22.0.5",
"@angular/cdk": "^22.0.5"
```

Il tema è in:

```text
src/material-theme.scss
```

ed è caricato da `angular.json` prima di Bootstrap e `styles.css`.

## 2. Regola dei componenti standalone

Per usare un componente Material devi importare il relativo modulo nel componente che lo usa.

La Home fa così:

```ts
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatCardModule]
})
```

## 3. Button

```html
<a mat-flat-button routerLink="/stanze">Scopri le camere</a>
<a mat-stroked-button routerLink="/spa">Entra nella SPA</a>
```

- `mat-flat-button`: azione principale;
- `mat-stroked-button`: azione secondaria;
- `mat-button`: azione leggera/testuale.

## 4. Card

```html
<mat-card appearance="outlined">
  <mat-card-content>
    <h3>Titolo</h3>
    <p>Contenuto</p>
  </mat-card-content>
</mat-card>
```

La Home genera le card con `@for`.

## 5. Personalizzare Material senza combatterlo

Nel CSS puoi usare una classe semantica:

```scss
.hero-primary.mat-mdc-button-base {
  --mdc-filled-button-container-color: #fffdf7;
  --mdc-filled-button-label-text-color: #102a2b;
}
```

Così mantieni:

- focus;
- stato disabled;
- semantica del vero bottone/link;
- comportamento Material;

ma scegli tu la palette.

## 6. Il prossimo passo consigliato: MatFormField

Per trasformare un input Bootstrap:

```html
<label>Email</label>
<input class="form-control" formControlName="email">
```

in Material:

```html
<mat-form-field appearance="outline">
  <mat-label>Email</mat-label>
  <input matInput formControlName="email">

  @if (form.controls.email.hasError('required')) {
    <mat-error>L'email è obbligatoria.</mat-error>
  }
</mat-form-field>
```

Servono:

```ts
MatFormFieldModule
MatInputModule
```

## 7. Componenti da provare in un progetto nuovo

Ordine didattico consigliato:

```text
MatButton
↓
MatFormField + MatInput + MatError
↓
MatSelect / MatCheckbox
↓
MatCard
↓
MatSnackBar
↓
MatDialog
↓
MatTable
↓
tema Material personalizzato
```

Il progetto nuovo da zero è il posto ideale per usarli in modo ancora più sistematico.

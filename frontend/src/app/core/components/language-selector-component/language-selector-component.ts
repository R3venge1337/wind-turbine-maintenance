import { CommonModule } from '@angular/common';
import { Component, computed, inject, Signal } from '@angular/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, startWith } from 'rxjs';

@Component({
  selector: 'app-language-selector-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './language-selector-component.html',
  styleUrl: './language-selector-component.scss',
})
export class LanguageSelectorComponent {
translate = inject(TranslateService)

public currentLang: Signal<string> = toSignal(
    this.translate.onLangChange.pipe(
      map((event: LangChangeEvent) => event.lang),
      startWith(this.translate.getCurrentLang() || 'pl')
    ),
    { initialValue: this.translate.getCurrentLang() || 'pl' }
  );

  useLang(lang: string) {
    this.translate.use(lang);
  }
}

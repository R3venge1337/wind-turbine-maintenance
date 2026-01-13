import { CommonModule } from '@angular/common';
import { Component, inject, Inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-add-turbine-modal-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule,TranslateModule],
  templateUrl: './add-turbine-modal-component.html',
  styleUrl: './add-turbine-modal-component.scss',
})
export class AddTurbineModalComponent {
private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<AddTurbineModalComponent>);
  
  // Odbieramy dane przekazane z głównego komponentu
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    if (data) {
      this.addForm.patchValue(data);
    }
  }

  addForm = this.fb.group({
    productId: ['', [Validators.required, Validators.minLength(3)]],
    city: ['', Validators.required],
    typeCode: ['L', Validators.required],
    latitude: [null as number | null, [Validators.required]],
    longitude: [null as number | null, [Validators.required]]
  });

  onPickFromMap() {
    this.dialogRef.close({ pickFromMap: true, payload: this.addForm.value });
  }

  onSubmit() {
    if (this.addForm.valid) {
      this.dialogRef.close({ save: true, payload: this.addForm.value });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}

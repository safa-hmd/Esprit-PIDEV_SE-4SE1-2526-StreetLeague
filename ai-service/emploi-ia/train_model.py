import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
import joblib
import matplotlib.pyplot as plt
import seaborn as sns

# ── TensorFlow / Keras ──────────────────────────────────────────
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, BatchNormalization
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau
from tensorflow.keras.optimizers import Adam

print("TensorFlow version:", tf.__version__)

# ── 1. Chargement ───────────────────────────────────────────────
df = pd.read_csv("dataset_features.csv")
X  = df.drop("label", axis=1)
y  = df["label"]

print("Features :", X.columns.tolist())
print("Shape X  :", X.shape)
print("Label distribution :\n", y.value_counts())

# ── 2. Split ────────────────────────────────────────────────────
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y)

print(f"\nTrain : {X_train.shape[0]} | Test : {X_test.shape[0]}")

# ── 3. Normalisation ────────────────────────────────────────────
scaler = StandardScaler()
X_train_sc = scaler.fit_transform(X_train)
X_test_sc  = scaler.transform(X_test)

# ── 4. Architecture Neural Network ──────────────────────────────
#
#   Input (n_features)
#       ↓
#   Dense(128, relu) + BatchNorm + Dropout(0.3)
#       ↓
#   Dense(64, relu)  + BatchNorm + Dropout(0.2)
#       ↓
#   Dense(32, relu)
#       ↓
#   Dense(1, sigmoid)  → probabilité 0-1
#
n_features = X_train_sc.shape[1]
print(f"\nNombre de features : {n_features}")

model = Sequential([
    # Couche 1 — large
    Dense(128, activation='relu', input_shape=(n_features,),
          kernel_regularizer=tf.keras.regularizers.l2(0.001)),
    BatchNormalization(),
    Dropout(0.3),

    # Couche 2
    Dense(64, activation='relu',
          kernel_regularizer=tf.keras.regularizers.l2(0.001)),
    BatchNormalization(),
    Dropout(0.2),

    # Couche 3
    Dense(32, activation='relu'),

    # Couche 4 — sortie binaire
    Dense(1, activation='sigmoid')
])

model.compile(
    optimizer=Adam(learning_rate=0.001),
    loss='binary_crossentropy',
    metrics=['accuracy', tf.keras.metrics.AUC(name='auc')]
)

model.summary()

# ── 5. Callbacks ────────────────────────────────────────────────
callbacks = [
    EarlyStopping(
        monitor='val_auc', patience=10,
        restore_best_weights=True, mode='max', verbose=1
    ),
    ReduceLROnPlateau(
        monitor='val_loss', factor=0.5,
        patience=5, min_lr=1e-6, verbose=1
    )
]

# ── 6. Entraînement ─────────────────────────────────────────────
print("\n🚀 Entraînement Neural Network en cours...")
history = model.fit(
    X_train_sc, y_train,
    validation_split=0.15,
    epochs=100,
    batch_size=256,
    callbacks=callbacks,
    verbose=1
)
print("✅ Entraînement terminé !")

# ── 7. Évaluation ───────────────────────────────────────────────
y_pred_proba = model.predict(X_test_sc).flatten()
y_pred       = (y_pred_proba >= 0.5).astype(int)

acc = accuracy_score(y_test, y_pred)
print(f"\n📊 Accuracy : {acc*100:.2f}%")
print(classification_report(y_test, y_pred,
      target_names=["Mauvais creneau", "Bon creneau"]))

# ── 8. Courbes d'apprentissage ───────────────────────────────────
fig, axes = plt.subplots(1, 2, figsize=(12, 4))

axes[0].plot(history.history['loss'],     label='Train Loss')
axes[0].plot(history.history['val_loss'], label='Val Loss')
axes[0].set_title('Loss'); axes[0].legend(); axes[0].grid(True)

axes[1].plot(history.history['accuracy'],     label='Train Acc')
axes[1].plot(history.history['val_accuracy'], label='Val Acc')
axes[1].set_title('Accuracy'); axes[1].legend(); axes[1].grid(True)

plt.tight_layout()
plt.savefig("training_curves.png")
print("📈 Courbes sauvegardées : training_curves.png")

# ── 9. Confusion Matrix ─────────────────────────────────────────
cm = confusion_matrix(y_test, y_pred)
plt.figure(figsize=(5, 4))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues',
            xticklabels=["Predit Mauvais", "Predit Bon"],
            yticklabels=["Reel Mauvais",   "Reel Bon"])
plt.title("Matrice de Confusion - Neural Network")
plt.tight_layout()
plt.savefig("confusion_matrix.png")
print("📊 Matrice sauvegardée : confusion_matrix.png")

# ── 10. Sauvegarde ──────────────────────────────────────────────
model.save("calendar_model_dl.keras")     # modèle Keras complet
joblib.dump(scaler, "scaler.pkl")         # même scaler (compatible api.py)

# Sauvegarde aussi en .h5 pour compatibilité
model.save("calendar_model_dl.h5")

print("\n✅ Modèle sauvegardé  : calendar_model_dl.keras")
print("✅ Scaler sauvegardé  : scaler.pkl")
print("\n🎯 Nouvelles features entraînées :")
print("   📍 distance_km, weather_score, temp_comfort")
print("   🏆 has_tournament, tournament_priority, field_tournament_fit")
print("\n🚀 Tout est prêt pour l'API Flask !")
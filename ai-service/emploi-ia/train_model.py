import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
import joblib
import matplotlib.pyplot as plt
import seaborn as sns

df = pd.read_csv("dataset_features.csv")
X = df.drop("label", axis=1)
y = df["label"]

print("Features :", X.columns.tolist())
print("Shape X :", X.shape)

X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y)

print(f"Train : {X_train.shape[0]} lignes | Test : {X_test.shape[0]} lignes")

scaler = StandardScaler()
X_train_sc = scaler.fit_transform(X_train)
X_test_sc  = scaler.transform(X_test)

print("\nEntrainement en cours... (1-2 minutes)")
model = GradientBoostingClassifier(
    n_estimators=200,
    max_depth=5,
    learning_rate=0.1,
    random_state=42
)
model.fit(X_train_sc, y_train)
print("Entrainement termine !")

y_pred = model.predict(X_test_sc)
acc = accuracy_score(y_test, y_pred)
print(f"\nAccuracy : {acc*100:.2f}%")
print(classification_report(y_test, y_pred,
      target_names=["Mauvais creneau", "Bon creneau"]))

cm = confusion_matrix(y_test, y_pred)
plt.figure(figsize=(5, 4))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues',
            xticklabels=["Predit Mauvais", "Predit Bon"],
            yticklabels=["Reel Mauvais",   "Reel Bon"])
plt.title("Matrice de Confusion")
plt.tight_layout()
plt.savefig("confusion_matrix.png")
print("Matrice sauvegardee : confusion_matrix.png")

joblib.dump(model,  "calendar_model.pkl")
joblib.dump(scaler, "scaler.pkl")
print("\nModele sauvegarde  : calendar_model.pkl")
print("Scaler sauvegarde  : scaler.pkl")
print("\nTout est pret pour l'API Flask !")
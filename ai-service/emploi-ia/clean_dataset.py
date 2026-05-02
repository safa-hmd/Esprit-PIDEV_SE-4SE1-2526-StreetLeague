import pandas as pd
from sklearn.utils import resample

df = pd.read_csv("dataset_raw.csv")
print("Avant nettoyage :", df.shape)

df = df.dropna()
df = df.drop_duplicates()
df = df[df["fatigue"].between(0, 10)]
df = df[df["hour"].between(0, 23)]
df = df[df["field_available"].isin([0, 1])]
df = df[df["field_pressure"].between(0, 1)]

counts = df["label"].value_counts()
print("Distribution avant :", counts.to_dict())

df_maj = df[df["label"] == 0]
df_min = df[df["label"] == 1]
df_min_up = resample(df_min, replace=True,
                     n_samples=len(df_maj), random_state=42)
df = pd.concat([df_maj, df_min_up])
df = df.sample(frac=1, random_state=42).reset_index(drop=True)

print("Apres reequilibrage :", df["label"].value_counts().to_dict())
df.to_csv("dataset_clean.csv", index=False)
print("Dataset nettoye :", df.shape)
print("Fichier dataset_clean.csv cree !")
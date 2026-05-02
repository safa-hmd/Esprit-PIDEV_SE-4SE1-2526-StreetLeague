import pandas as pd

df = pd.read_csv("dataset_clean.csv")

df["win_ratio"]    = df["victories"] / (df["victories"] + df["defeats"] + 1)
df["team_load"]    = df["matches_week"] + df["trainings_week"]
df["is_weekend"]   = (df["day"] >= 5).astype(int)
df["is_peak_hour"] = df["hour"].apply(lambda h: 1 if 16 <= h <= 20 else 0)
df["field_fits"]   = (df["field_capacity"] >= df["player_count"]).astype(int)

df = df.drop(columns=["victories", "defeats"])

print("Features finales :", df.columns.tolist())
print("Shape :", df.shape)
df.to_csv("dataset_features.csv", index=False)
print("Fichier dataset_features.csv cree !")
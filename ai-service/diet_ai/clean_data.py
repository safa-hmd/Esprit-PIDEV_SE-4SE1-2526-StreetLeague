import pandas as pd
from sklearn.preprocessing import LabelEncoder, StandardScaler
import joblib

# 1. Load dataset
df = pd.read_csv("diet_dataset.csv")
print(f"📊 Loaded: {len(df)} rows")

# 2. Check nulls
print(f"❌ Nulls:\n{df.isnull().sum()}")
df.dropna(inplace=True)

# 3. Encode goal (label)
le_goal = LabelEncoder()
df["goal_encoded"] = le_goal.fit_transform(df["goal"])
joblib.dump(le_goal, "goal_encoder.pkl")

# 4. Normalize features
scaler = StandardScaler()
features = ["age", "bmi"]
df[features] = scaler.fit_transform(df[features])
joblib.dump(scaler, "scaler.pkl")

# 5. Save cleaned data
df.to_csv("diet_cleaned.csv", index=False)
print(f"✅ Cleaned data saved: {len(df)} rows")
print(df.head())
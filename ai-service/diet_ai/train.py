import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
import joblib

# 1. Load cleaned data
df = pd.read_csv("diet_cleaned.csv")

# 2. Features & Label
X = df[["age", "bmi"]]
y = df["goal_encoded"]

# 3. Split 80% train / 20% test
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42
)

# 4. Train Random Forest
model = RandomForestClassifier(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

# 5. Evaluate
y_pred = model.predict(X_test)
accuracy = accuracy_score(y_test, y_pred)
print(f"✅ Accuracy: {accuracy * 100:.2f}%")
print("\n📊 Classification Report:")
print(classification_report(y_test, y_pred,
      target_names=["GAIN_MUSCLE", "LOSE_WEIGHT", "MAINTAIN"]))

# 6. Save model
joblib.dump(model, "diet_model.pkl")
print("💾 Model saved: diet_model.pkl")
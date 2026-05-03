import pandas as pd
import numpy as np

np.random.seed(42)
n = 5000

age = np.random.randint(15, 60, n)

bmi = np.concatenate([
    np.random.uniform(13, 18.4, 1200),
    np.random.uniform(18.5, 24.9, 1500),
    np.random.uniform(25, 29.9, 1300),
    np.random.uniform(30, 45, 1000),
])

np.random.shuffle(bmi)

bmi_noisy = bmi + np.random.normal(0, 1.5, n)

def get_goal(bmi):
    if bmi < 18.5:
        return "GAIN_MUSCLE"
    elif 18.5 <= bmi <= 24.9:
        return "MAINTAIN"
    else:
        return "LOSE_WEIGHT"

goal = [get_goal(b) for b in bmi_noisy]

df = pd.DataFrame({
    "age": age,
    "bmi": bmi_noisy.round(2),
    "goal": goal
})

df.to_csv("diet_dataset.csv", index=False)
print(f"✅ Dataset generated: {len(df)} rows")
print(df["goal"].value_counts())
print(df.head())
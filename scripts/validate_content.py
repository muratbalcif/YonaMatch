#!/usr/bin/env python3
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
source = root / "shared-content/levels/memory.json"
data = json.loads(source.read_text())
assert data["schemaVersion"] == 1
assert data["gameType"] == "memory"
levels = data["levels"]
assert len(levels) == 20
assert [v["order"] for v in levels] == list(range(1, 21))
assert len({v["id"] for v in levels}) == 20
assert all(v["pairs"] >= 2 and v["idealAttempts"] >= v["pairs"] for v in levels)
for path in ["android/app/src/main/assets/levels/memory.json",
             "ios/Sources/YonaMatchCore/Resources/levels/memory.json"]:
    assert json.loads((root / path).read_text()) == data, f"Platform copy out of sync: {path}"
print("Content validation passed: 20 levels, 2 platform copies")

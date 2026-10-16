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

shadow_source = root / "shared-content/levels/shadow.json"
shadow = json.loads(shadow_source.read_text())
assert shadow["schemaVersion"] == 1 and shadow["gameType"] == "shadow"
assert len(shadow["levels"]) == 20
assert [v["order"] for v in shadow["levels"]] == list(range(1, 21))
catalog_ids = {item["id"] for item in shadow["catalog"]}
assert len(catalog_ids) == len(shadow["catalog"])
for level in shadow["levels"]:
    assert len(level["items"]) == len(set(level["items"]))
    assert set(level["items"]).issubset(catalog_ids)
    assert level["idealAttempts"] >= len(level["items"])
for platform_path in [
    "android/app/src/main/assets/levels/shadow.json",
    "ios/Sources/YonaMatchCore/Resources/levels/shadow.json",
]:
    assert json.loads((root / platform_path).read_text()) == shadow, platform_path
print("Shadow validation passed: 20 levels, both platforms")

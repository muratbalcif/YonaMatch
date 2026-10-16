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

for game_type in ("shape", "color", "halves"):
    content = json.loads((root / f"shared-content/levels/{game_type}.json").read_text())
    assert content["schemaVersion"] == 1 and content["gameType"] == game_type
    assert len(content["levels"]) == 20
    assert [entry["order"] for entry in content["levels"]] == list(range(1, 21))
    ids = {item["id"] for item in content["catalog"]}
    assert len(ids) == len(content["catalog"])
    for entry in content["levels"]:
        assert len(entry["items"]) == len(set(entry["items"]))
        assert set(entry["items"]).issubset(ids)
        assert entry["idealAttempts"] >= len(entry["items"])
    for platform in ("android/app/src/main/assets/levels", "ios/Sources/YonaMatchCore/Resources/levels"):
        assert json.loads((root / platform / f"{game_type}.json").read_text()) == content
print("Shape and color validation passed: 40 levels, both platforms")

association = json.loads((root / "shared-content/levels/association.json").read_text())
assert association["schemaVersion"] == 1 and association["gameType"] == "association"
assert len(association["levels"]) == 20
assert [entry["order"] for entry in association["levels"]] == list(range(1, 21))
association_ids = {item["id"] for item in association["catalog"]}
assert len(association_ids) == len(association["catalog"])
for entry in association["levels"]:
    sources = [pair["source"] for pair in entry["pairs"]]
    targets = [pair["target"] for pair in entry["pairs"]]
    assert len(sources) == len(set(sources))
    assert len(targets) == len(set(targets))
    assert set(sources + targets).issubset(association_ids)
    assert entry["idealAttempts"] >= len(entry["pairs"])
for platform in ("android/app/src/main/assets/levels", "ios/Sources/YonaMatchCore/Resources/levels"):
    assert json.loads((root / platform / "association.json").read_text()) == association
print("Association validation passed: 20 levels, both platforms")

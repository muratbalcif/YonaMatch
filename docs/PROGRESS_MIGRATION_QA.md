# Six-game progress migration and QA

## Scoring contract (Android and iOS)
- Completed with a hint: 1 star.
- Completed without a hint and attempts <= level idealAttempts: 3 stars.
- Completed without a hint with more attempts: 2 stars.
- Never lower previously earned best stars.
- Unlock min(20, completedLevel + 1) without reducing previously unlocked levels.
- All six games have independent progress and per-level star records.

## Migration
- New Android data: SharedPreferences yonamatch_game_progress_v1; migration flag migration_v1_complete.
- Existing Android matching unlocks: yonamatch_matching; shadow: yonamatch_shadow. Memory remains in its original ProgressRepository as well.
- New iOS data: UserDefaults keys ym_progress_<game> and ym_stars_<game>_<level>; migration flag ym_migration_v1_done.
- Existing iOS unlocks: <game>_unlocked; memory remains in its original ProgressRepository as well.
- Migration is one-time and only increases unlocked levels. Older builds did not store stars for all six games; do not fabricate historical star ratings.

## Manual release checks
1. Upgrade from an older installed build with unlocked levels, without uninstalling. Verify unlocked levels remain.
2. Complete a level with ideal attempts: three stars.
3. Complete a level after mistakes: two stars.
4. Use the lightbulb hint, then finish: one star.
5. Replay a three-star level with a hint: best-star history stays three.
6. Force-stop and relaunch; inspect unlocked levels and saved stars.
7. Repeat on Android and iOS for memory, shadow, shape, color, halves, association.
8. Complete level 20; confirm navigation returns to the level map.
9. Confirm six games remain independent and no saved level is locked again.

## Automation
- iOS GameProgressStoreTests covers scoring, monotonic records, migration and simulated relaunch.
- Content CI validates all bundled game catalogs.
- Android instrumentation tests for SharedPreferences migration and on-device navigation remain to be implemented.
- CI/build status must be checked in GitHub Actions; repository edits alone do not establish passing builds.

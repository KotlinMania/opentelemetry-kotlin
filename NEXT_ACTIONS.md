# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/35 (8.6%)
- **Function parity:** 96/356 matched (target 182) — 27.0%
- **Class/type parity:** 24/91 matched (target 34) — 26.4%
- **Combined symbol parity:** 120/447 matched (target 216) — 26.8%
- **Average inline-code cosine:** 0.52 (function body across 3 matched files)
- **Average documentation cosine:** 0.39 (doc text across 3 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. context

- **Target:** `opentelemetry.Context`
- **Similarity:** 0.63
- **Dependents:** 1
- **Priority Score:** 1024703.7
- **Functions:** 38/40 matched (target 49)
- **Missing functions:** `nested_operation`, `create_a_future`
- **Types:** 7/7 matched (target 8)
- **Missing types:** _none_
- **Tests:** 16/18 matched
- **Lint issues:** 1

### 2. common

- **Target:** `opentelemetry.Common`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 13606.3
- **Functions:** 26/27 matched (target 84)
- **Missing functions:** `hash_helper`
- **Types:** 9/9 matched (target 17)
- **Missing types:** _none_
- **Tests:** 5/6 matched

### 3. baggage

- **Target:** `opentelemetry.Baggage`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 4004.4
- **Functions:** 32/32 matched (target 49)
- **Missing functions:** _none_
- **Types:** 8/8 matched (target 9)
- **Missing types:** _none_
- **Tests:** 9/9 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `global.mod` | `global.Mod` | 0 | `global/mod.rs` | `global/Mod.kt` |
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |
| `logs.mod` | `logs.Mod` | 0 | `logs/mod.rs` | `logs/Mod.kt` |
| `instruments.mod` | `metrics.instruments.Mod` | 0 | `metrics/instruments/mod.rs` | `metrics/instruments/Mod.kt` |
| `metrics.mod` | `metrics.Mod` | 0 | `metrics/mod.rs` | `metrics/Mod.kt` |
| `propagation.mod` | `propagation.Mod` | 0 | `propagation/mod.rs` | `propagation/Mod.kt` |
| `testing.mod` | `testing.Mod` | 0 | `testing/mod.rs` | `testing/Mod.kt` |
| `trace.mod` | `trace.Mod` | 0 | `trace/mod.rs` | `trace/Mod.kt` |


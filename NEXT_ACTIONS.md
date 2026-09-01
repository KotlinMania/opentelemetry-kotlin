# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/41 (7.3%)
- **Function parity:** 96/378 matched (target 182) — 25.4%
- **Class/type parity:** 24/92 matched (target 34) — 26.1%
- **Combined symbol parity:** 120/470 matched (target 216) — 25.5%
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

### 1. opentelemetry.context

- **Target:** `opentelemetry.Context`
- **Similarity:** 0.63
- **Dependents:** 2
- **Priority Score:** 2024703.6
- **Functions:** 38/40 matched (target 49)
- **Missing functions:** `nested_operation`, `create_a_future`
- **Types:** 7/7 matched (target 8)
- **Missing types:** _none_
- **Tests:** 16/18 matched

### 2. opentelemetry.baggage

- **Target:** `opentelemetry.Baggage`
- **Similarity:** 0.56
- **Dependents:** 1
- **Priority Score:** 1004004.4
- **Functions:** 32/32 matched (target 49)
- **Missing functions:** _none_
- **Types:** 8/8 matched (target 9)
- **Missing types:** _none_
- **Tests:** 9/9 matched

### 3. opentelemetry.common

- **Target:** `opentelemetry.Common`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 13606.3
- **Functions:** 26/27 matched (target 84)
- **Missing functions:** `hash_helper`
- **Types:** 9/9 matched (target 17)
- **Missing types:** _none_
- **Tests:** 5/6 matched

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
| `global.mod` | `opentelemetry.src.global.Mod` | 0 | `opentelemetry/src/global/mod.rs` | `opentelemetry/src/global/Mod.kt` |
| `opentelemetry.lib` | `opentelemetry.src.Lib` | 0 | `opentelemetry/src/lib.rs` | `opentelemetry/src/Lib.kt` |
| `logs.mod` | `opentelemetry.src.logs.Mod` | 0 | `opentelemetry/src/logs/mod.rs` | `opentelemetry/src/logs/Mod.kt` |
| `instruments.mod` | `opentelemetry.src.metrics.instruments.Mod` | 0 | `opentelemetry/src/metrics/instruments/mod.rs` | `opentelemetry/src/metrics/instruments/Mod.kt` |
| `metrics.mod` | `opentelemetry.src.metrics.Mod` | 0 | `opentelemetry/src/metrics/mod.rs` | `opentelemetry/src/metrics/Mod.kt` |
| `propagation.mod` | `opentelemetry.src.propagation.Mod` | 0 | `opentelemetry/src/propagation/mod.rs` | `opentelemetry/src/propagation/Mod.kt` |
| `testing.mod` | `opentelemetry.src.testing.Mod` | 0 | `opentelemetry/src/testing/mod.rs` | `opentelemetry/src/testing/Mod.kt` |
| `trace.mod` | `opentelemetry.src.trace.Mod` | 0 | `opentelemetry/src/trace/mod.rs` | `opentelemetry/src/trace/Mod.kt` |


# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/35 (5.7%)
- **Function parity:** 13/357 matched (target 38) — 3.6%
- **Class/type parity:** 8/91 matched (target 16) — 8.8%
- **Combined symbol parity:** 21/448 matched (target 54) — 4.7%
- **Average inline-code cosine:** 0.10 (function body across 2 matched files)
- **Average documentation cosine:** 0.52 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. baggage

- **Target:** `opentelemetry.Baggage [PROVENANCE-FALLBACK]`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 324009.1
- **Functions:** 6/32 matched (target 15)
- **Missing functions:** `get_default_baggage`, `get_with_metadata`, `insert_with_metadata`, `len`, `iter`, `is_key_valid`, `key_value_metadata_bytes_size`, `next`, `into_iter`, `from_iter`, `encode`, `fmt`, `with_baggage`, `current_with_baggage`, `with_cleared_baggage`, `baggage`, `as_str`, `insert_non_ascii_key`, `test_ascii_values`, `insert_too_much_baggage`, `insert_pairs_length_exceed`, `serialize_baggage_as_string`, `replace_existing_key`, `test_crud_operations`, `test_insert_invalid_key`, `test_context_clear_baggage`
- **Types:** 2/8 matched (target 2)
- **Missing types:** `Iter`, `Item`, `IntoIter`, `BaggageExt`, `BaggageContextValue`, `KeyValueMetadata`
- **Tests:** 0/9 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/opentelemetry/src/baggage.rs` vs expected `baggage.rs`
- **Proposed provenance header:** `// port-lint: source baggage.rs` (current: `// port-lint: source tmp/opentelemetry/src/baggage.rs`)
- **Lint issues:** 1

### 2. common

- **Target:** `opentelemetry.Common [PROVENANCE-FALLBACK]`
- **Similarity:** 0.10
- **Dependents:** 0
- **Priority Score:** 233609.0
- **Functions:** 7/27 matched (target 23)
- **Missing functions:** `from_static_str`, `as_str`, `fmt`, `borrow`, `as_ref`, `partial_cmp`, `cmp`, `eq`, `hash`, `display_array_str`, `name`, `version`, `schema_url`, `attributes`, `kv_float_equality`, `kv_float_hash`, `hash_helper`, `instrumentation_scope_equality`, `instrumentation_scope_equality_attributes_diff_order`, `instrumentation_scope_equality_different_attributes`
- **Types:** 6/9 matched (target 14)
- **Missing types:** `OtelString`, `Array`, `F64Hashable`
- **Tests:** 0/6 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tmp/opentelemetry/src/common.rs` vs expected `common.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:tmp/opentelemetry/src/common.rs` vs expected `common.rs`
- **Proposed provenance header:** `// port-lint: source common.rs` (current: `// port-lint: source tmp/opentelemetry/src/common.rs`)
- **Proposed provenance header:** `// port-lint: tests common.rs` (current: `// port-lint: tests tmp/opentelemetry/src/common.rs`)
- **Lint issues:** 2

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


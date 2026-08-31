# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/41 (7.3%)
- **Function parity:** 96/424 matched (target 182) — 22.6%
- **Class/type parity:** 24/103 matched (target 34) — 23.3%
- **Combined symbol parity:** 120/527 matched (target 216) — 22.8%
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


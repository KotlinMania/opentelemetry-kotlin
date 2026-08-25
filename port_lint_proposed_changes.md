# port-lint Proposed Changes

**Generated:** 2026-08-25
**Source:** tmp/opentelemetry/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/opentelemetry

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/opentelemetry/Baggage.kt` | `// port-lint: source tmp/opentelemetry/src/baggage.rs` | `// port-lint: source baggage.rs` | `baggage.rs` | `port-lint provenance header matched only after fallback normalization: 'tmp/opentelemetry/src/baggage.rs' vs expected 'baggage.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/opentelemetry/Common.kt` | `// port-lint: source tmp/opentelemetry/src/common.rs` | `// port-lint: source common.rs` | `common.rs` | `port-lint provenance header matched only after fallback normalization: 'tmp/opentelemetry/src/common.rs' vs expected 'common.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/opentelemetry/CommonTest.kt` | `// port-lint: tests tmp/opentelemetry/src/common.rs` | `// port-lint: tests common.rs` | `common.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:tmp/opentelemetry/src/common.rs' vs expected 'common.rs'` |

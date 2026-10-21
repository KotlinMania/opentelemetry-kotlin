package io.github.kotlinmania.opentelemetry.time

import kotlin.time.Clock
import kotlin.time.Instant

public object Time {
    public fun now(): Instant = Clock.System.now()
}

public fun now(): Instant = Time.now()

package ru.nsu.diff.engine.lang

sealed class ContextLevel

object EmptyContext: ContextLevel()

// Kotlin

sealed class KotlinContextLevel: ContextLevel()

object TopLevel : KotlinContextLevel()
object ClassMember : KotlinContextLevel()
object Local: KotlinContextLevel()
object Expression: KotlinContextLevel()

// Java

sealed class JavaContextLevel: ContextLevel()


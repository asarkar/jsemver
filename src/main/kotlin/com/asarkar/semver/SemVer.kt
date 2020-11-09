package com.asarkar.semver

import java.util.Objects

/**
 * SemVer identifier.
 *
 * @author Abhijit Sarkar
 */
sealed class Id(open val value: String) : Comparable<Id> {
    companion object {
        fun parseId(str: String, allowLeadingZeroes: Boolean): Id {
            return try {
                str.toLong()
                NumericId(str, allowLeadingZeroes)
            } catch (nfe: NumberFormatException) {
                AlphanumericId(str)
            }
        }
    }
}

/**
 * SemVer alphanumeric identifier.
 * MUST NOT be empty.
 * MUST comprise only ASCII alphanumerics and hyphens \[0-9A-Za-z-\].
 * Compared lexically in ASCII sort order.
 *
 * @author Abhijit Sarkar
 */
class AlphanumericId(override val value: String) : Id(value), Comparable<Id> {
    init {
        require(value.isNotEmpty()) { "Identifier MUST NOT be empty" }
        require(value.matches("[0-9A-Za-z\\-]+".toRegex())) {
            "Identifier MUST comprise only ASCII alphanumerics and hyphens"
        }
    }

    override fun toString() = value
    override fun compareTo(other: Id): Int {
        return if (other is NumericId) 1
        else {
            other as AlphanumericId
            value.compareTo(other.value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AlphanumericId) return false
        if (other === this) return true
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

/**
 * SemVer numeric identifier.
 * MUST NOT be empty.
 * MUST be a non-negative integer. Depending on the context, may or may not contain leading zeroes.
 * Compared numerically. Has lower precedence than alphanumeric identifiers.
 *
 * @author Abhijit Sarkar
 */
class NumericId(override val value: String, allowLeadingZeroes: Boolean) : Id(value), Comparable<Id> {
    constructor(value: Any, allowLeadingZeroes: Boolean) : this(value.toString(), allowLeadingZeroes)
    constructor(value: Any) : this(value.toString(), false)

    init {
        require(value.isNotEmpty()) { "Identifier MUST NOT be empty" }
        require(value.matches("\\d+".toRegex())) {
            "Numeric identifier MUST be a non-negative integer"
        }
        if (!allowLeadingZeroes && value.length > 1 && value.dropWhile { it == '0' }.length < value.length) {
            throw IllegalArgumentException("Numeric identifier MUST NOT include leading zeroes")
        }
    }

    fun toInt() = value.toInt()

    override fun toString() = value
    override fun compareTo(other: Id): Int {
        return if (other is AlphanumericId) -1
        else {
            other as NumericId
            value.toInt().compareTo(other.value.toInt())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NumericId) return false
        if (other === this) return true
        return value.toInt() == other.value.toInt()
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

/**
 * SemVer normal version.
 * MUST take the form X.Y.Z where X, Y, and Z are non-negative integers, and MUST NOT contain leading zeroes.
 * X is the major version, Y is the minor version, and Z is the patch version.
 *
 * @author Abhijit Sarkar
 */
class Normal(val major: NumericId, val minor: NumericId, val patch: NumericId) : Comparable<Normal> {
    constructor(major: Any, minor: Any, patch: Any) : this(
        NumericId(major, false),
        NumericId(minor, false),
        NumericId(patch, false)
    )

    private val components = listOf(major, minor, patch)
    override fun toString() = components.joinToString(separator = ".")
    override fun compareTo(other: Normal): Int {
        return components
            .zip(other.components)
            .map { it.first.compareTo(it.second) }
            .firstOrNull { it != 0 }
            ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Normal) return false
        if (other === this) return true
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(*components.toTypedArray())
    }

    fun withMajor(major: Any): Normal = Normal(NumericId(major, false), minor, patch)
    fun withMinor(minor: Any): Normal = Normal(major, NumericId(minor, false), patch)
    fun withPatch(patch: Any): Normal = Normal(major, minor, NumericId(patch, false))
}

/**
 * SemVer identifier container.
 *
 * @author Abhijit Sarkar
 */
sealed class Ids(open val ids: List<Id>) {
    override fun toString() = ids.joinToString(separator = ".")
}

/**
 * SemVer pre-release version.
 * Identifiers MUST comprise only ASCII alphanumerics and hyphens \[0-9A-Za-z-\].
 * Identifiers MUST NOT be empty.
 * Numeric identifiers MUST NOT include leading zeroes.
 * Pre-release versions have a lower precedence than the associated normal version.
 *
 * @author Abhijit Sarkar
 */
class PreRelease(override val ids: List<Id>) : Ids(ids), Comparable<PreRelease> {
    constructor(vararg ids: String) : this(ids.map { Id.parseId(it, false) })

    override fun compareTo(other: PreRelease): Int {
        val result = ids
            .zip(other.ids)
            .map { it.first.compareTo(it.second) }
            .firstOrNull { it != 0 }
            ?: 0
        return if (result == 0) ids.size.compareTo(other.ids.size)
        else result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PreRelease) return false
        if (other === this) return true
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(*ids.toTypedArray())
    }
}

/**
 * SemVer build metadata.
 * Identifiers MUST comprise only ASCII alphanumerics and hyphens \[0-9A-Za-z-\].
 * Identifiers MUST NOT be empty.
 * Ignored when determining version precedence.
 *
 * @author Abhijit Sarkar
 */
class Build(override val ids: List<Id>) : Ids(ids) {
    constructor(vararg ids: String) : this(ids.map { Id.parseId(it, true) })
}

/**
 * Semantic version.
 * MUST contain normal version.
 * May contain pre-release version and build metadata.
 *
 * @author Abhijit Sarkar
 */
class SemVer(val normal: Normal, val preRelease: PreRelease?, val build: Build?) : Comparable<SemVer> {
    constructor(normal: Normal) : this(normal, null, null)

    private val components = listOf(normal, preRelease)

    override fun toString(): String {
        val s = StringBuilder().append(normal)
        if (preRelease != null) s.append('-').append(preRelease)
        if (build != null) s.append('+').append(build)
        return s.toString()
    }

    fun hasPreRelease(): Boolean = preRelease != null
    fun hasBuild(): Boolean = build != null

    override fun compareTo(other: SemVer): Int {
        val result = normal.compareTo(other.normal)
        return if (result == 0) {
            if (preRelease != null && other.preRelease == null) -1
            else if (preRelease == null && other.preRelease != null) 1
            else if (preRelease != null && other.preRelease != null) preRelease.compareTo(other.preRelease)
            else 0
        } else result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SemVer) return false
        if (other === this) return true
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(*components.toTypedArray())
    }

    val major = normal.major.toInt()
    val minor = normal.minor.toInt()
    val patch = normal.patch.toInt()

    fun withMajor(major: Any): SemVer = SemVer(normal.withMajor(major), preRelease, build)
    fun withMinor(minor: Any): SemVer = SemVer(normal.withMinor(minor), preRelease, build)
    fun withPatch(patch: Any): SemVer = SemVer(normal.withPatch(patch), preRelease, build)
    fun withNormal(normal: Normal): SemVer = SemVer(normal, preRelease, build)
    fun withPreRelease(preRelease: PreRelease?): SemVer = SemVer(normal, preRelease, build)
    fun withBuild(build: Build?): SemVer = SemVer(normal, preRelease, build)

    companion object {
        fun parse(str: String): SemVer = SemVerParser().parse(str)
        fun isValid(str: String): Boolean {
            return try {
                SemVerParser().parse(str)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}

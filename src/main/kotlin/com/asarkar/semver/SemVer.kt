package com.asarkar.semver

import java.util.Objects

/**
 * SemVer identifier.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
sealed class Id(open val value: String) : Comparable<Id> {
    companion object {
        fun parse(str: String, allowLeadingZeroes: Boolean): Id {
            return try {
                str.toULong()
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
 * MUST comprise only ASCII alphanumerics and hyphens &#91;0-9A-Za-z-&#93;.
 * Compared lexically in ASCII sort order.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
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
 * @since 1.0.0
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

    fun toULong() = value.toULong()

    override fun toString() = value
    override fun compareTo(other: Id): Int {
        return if (other is AlphanumericId) -1
        else {
            other as NumericId
            value.toULong().compareTo(other.value.toULong())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NumericId) return false
        if (other === this) return true
        return value.toULong() == other.value.toULong()
    }

    override fun hashCode(): Int {
        return value.toULong().hashCode()
    }
}

/**
 * SemVer normal version.
 * MUST take the form X.Y.Z where X, Y, and Z are non-negative integers, and MUST NOT contain leading zeroes.
 * X is the major version, Y is the minor version, and Z is the patch version.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
class NormalVersion(val majorVersion: NumericId, val minorVersion: NumericId, val patchVersion: NumericId) : Comparable<NormalVersion> {
    constructor(major: Any, minor: Any, patch: Any) : this(
        NumericId(major, false),
        NumericId(minor, false),
        NumericId(patch, false)
    )

    private val components = listOf(majorVersion, minorVersion, patchVersion)
    override fun toString() = components.joinToString(separator = ".")
    override fun compareTo(other: NormalVersion): Int {
        return components
            .zip(other.components)
            .map { it.first.compareTo(it.second) }
            .firstOrNull { it != 0 }
            ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NormalVersion) return false
        if (other === this) return true
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(*components.toTypedArray())
    }

    fun withMajorVersion(major: Any): NormalVersion = NormalVersion(NumericId(major, false), minorVersion, patchVersion)
    fun withMinorVersion(minor: Any): NormalVersion = NormalVersion(majorVersion, NumericId(minor, false), patchVersion)
    fun withPatchVersion(patch: Any): NormalVersion = NormalVersion(majorVersion, minorVersion, NumericId(patch, false))
}

/**
 * SemVer identifier container.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
sealed class Ids(open val ids: List<Id>) {
    override fun toString() = ids.joinToString(separator = ".")
}

/**
 * SemVer pre-release version.
 * MUST comprise only ASCII alphanumerics and hyphens &#91;0-9A-Za-z-&#93;.
 * MUST NOT be empty.
 * Numeric identifiers MUST NOT include leading zeroes.
 * Pre-release versions have a lower precedence than the associated normal version.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 *
 */
class PreReleaseVersion(override val ids: List<Id>) : Ids(ids), Comparable<PreReleaseVersion> {
    constructor(vararg ids: String) : this(ids.map { Id.parse(it, false) })

    override fun compareTo(other: PreReleaseVersion): Int {
        val result = ids
            .zip(other.ids)
            .map { it.first.compareTo(it.second) }
            .firstOrNull { it != 0 }
            ?: 0
        return if (result == 0) ids.size.compareTo(other.ids.size)
        else result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PreReleaseVersion) return false
        if (other === this) return true
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(*ids.toTypedArray())
    }
}

/**
 * SemVer build metadata.
 * Identifiers MUST comprise only ASCII alphanumerics and hyphens &#91;0-9A-Za-z-&#93;.
 * Identifiers MUST NOT be empty.
 * Ignored when determining version precedence.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
class BuildMetadata(override val ids: List<Id>) : Ids(ids) {
    constructor(vararg ids: String) : this(ids.map { Id.parse(it, true) })
}

/**
 * Semantic version.
 * MUST contain normal version.
 * May contain pre-release version and build metadata.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
class SemVer(val normalVersion: NormalVersion, val preReleaseVersion: PreReleaseVersion?, val buildMetadata: BuildMetadata?) : Comparable<SemVer> {
    constructor(normalVersion: NormalVersion) : this(normalVersion, null, null)

    override fun toString(): String {
        val s = StringBuilder().append(normalVersion)
        if (preReleaseVersion != null) s.append('-').append(preReleaseVersion)
        if (buildMetadata != null) s.append('+').append(buildMetadata)
        return s.toString()
    }

    fun hasPreReleaseVersion(): Boolean = preReleaseVersion != null
    fun hasBuildMetadata(): Boolean = buildMetadata != null

    override fun compareTo(other: SemVer): Int {
        val result = normalVersion.compareTo(other.normalVersion)
        return if (result == 0) {
            if (preReleaseVersion != null && other.preReleaseVersion == null) -1
            else if (preReleaseVersion == null && other.preReleaseVersion != null) 1
            else if (preReleaseVersion != null && other.preReleaseVersion != null) preReleaseVersion.compareTo(other.preReleaseVersion)
            else 0
        } else result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SemVer) return false
        if (other === this) return true
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(normalVersion, preReleaseVersion)
    }

    val majorVersion = normalVersion.majorVersion.toULong()
    val minorVersion = normalVersion.minorVersion.toULong()
    val patchVersion = normalVersion.patchVersion.toULong()

    fun withMajorVersion(major: Any): SemVer = SemVer(normalVersion.withMajorVersion(major), preReleaseVersion, buildMetadata)
    fun withMinorVersion(minor: Any): SemVer = SemVer(normalVersion.withMinorVersion(minor), preReleaseVersion, buildMetadata)
    fun withPatchVersion(patch: Any): SemVer = SemVer(normalVersion.withPatchVersion(patch), preReleaseVersion, buildMetadata)
    fun withNormalVersion(normalVersion: NormalVersion): SemVer = SemVer(normalVersion, preReleaseVersion, buildMetadata)
    fun withPreReleaseVersion(preReleaseVersion: PreReleaseVersion?): SemVer = SemVer(normalVersion, preReleaseVersion, buildMetadata)
    fun withBuildMetadata(buildMetadata: BuildMetadata?): SemVer = SemVer(normalVersion, preReleaseVersion, buildMetadata)

    companion object {
        /**
         * Parses [input] to produce a [SemVer].
         * @throws IllegalArgumentException if the input couldn't be parsed.
         */
        fun parse(input: String): SemVer = SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(input)

        /**
         * Parses [input] to determine whether it's valid a [SemVer].
         */
        fun isValid(input: String): Boolean {
            return try {
                parse(input)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}

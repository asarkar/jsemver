package com.asarkar.semver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.Locale

class SemVerTest {
    @ParameterizedTest
    @MethodSource("versionsProvider")
    fun testPrecedence(args: List<String>) {
        val versions = args
            .shuffled()
            .map(SemVer::parse)
            .sorted()
            .map { it.toString() }
        assertThat(versions).containsExactlyElementsOf(args)
    }

    @Test
    fun testToString() {
        val locale = Locale.getDefault()
        Locale.setDefault(Locale("hi", "IN"))
        try {
            assertThat(SemVer.parse("2.2.0").toString()).isEqualTo("2.2.0")
        } finally {
            Locale.setDefault(locale)
        }
    }

    @Test
    fun testBuilders() {
        val v = SemVer(NormalVersion(1, 0, 0))
        assertThat(v.withMajorVersion(2).withMinorVersion(1).withPatchVersion(1).toString()).isEqualTo("2.1.1")
        assertThat(v.withNormalVersion(NormalVersion(2, 1, 1)).toString()).isEqualTo("2.1.1")
        assertThat(v.withPreReleaseVersion(PreReleaseVersion("beta", "1")).toString()).isEqualTo("1.0.0-beta.1")
        assertThat(v.withBuildMetadata(BuildMetadata("001")).toString()).isEqualTo("1.0.0+001")
        assertThat(v.withPreReleaseVersion(PreReleaseVersion("alpha")).withBuildMetadata(BuildMetadata("001")).toString())
            .isEqualTo("1.0.0-alpha+001")
    }

    @Test
    fun testIsValid() {
        assertThat(SemVer.isValid("1.0.0")).isTrue
        assertThat(SemVer.isValid("1")).isFalse
    }

    @Test
    fun testEquals() {
        assertThat(SemVer.parse("1.0.0-alpha+1")).isEqualTo(SemVer.parse("1.0.0-alpha+1"))
        assertThat(NormalVersion(1, 0, 0)).isEqualTo(NormalVersion("1", "0", "0"))
        assertThat(PreReleaseVersion("alpha")).isEqualTo(PreReleaseVersion("alpha"))
        assertThat(AlphanumericId("alpha")).isEqualTo(AlphanumericId("alpha"))
        assertThat(NumericId(1)).isEqualTo(NumericId(1))
    }

    companion object {
        @JvmStatic
        fun versionsProvider(): List<List<String>> {
            return listOf(
                listOf("1.0.0", "2.0.0", "2.1.0", "2.1.1"),
                listOf("1.0.0-alpha", "1.0.0"),
                listOf(
                    "1.0.0-alpha", "1.0.0-alpha.1", "1.0.0-alpha.beta", "1.0.0-beta",
                    "1.0.0-beta.2", "1.0.0-beta.11", "1.0.0-rc.1", "1.0.0"
                ),
                listOf("1.0.0-b10", "1.0.0-b9")
            )
        }
    }
}

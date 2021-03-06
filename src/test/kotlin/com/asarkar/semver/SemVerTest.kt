package com.asarkar.semver

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.Locale

class SemVerTest {
    @ParameterizedTest
    @MethodSource("createProvider")
    fun testCreate(v: SemVer) {
        assertThat(v.majorVersion).isEqualTo(1uL)
        assertThat(v.minorVersion).isEqualTo(0uL)
        assertThat(v.patchVersion).isEqualTo(0uL)
        if (v.hasPreReleaseVersion()) {
            assertThat(v.preReleaseVersion.toString()).isEqualTo("alpha")
        }
        if (v.hasBuildMetadata()) {
            assertThat(v.buildMetadata.toString()).isEqualTo("1")
        }
    }

    @ParameterizedTest
    @MethodSource("precedenceProvider")
    fun testPrecedence(args: List<String>) {
        val versions = args
            .shuffled()
            .map(SemVer::parse)
            .sorted()
            .map { it.toString() }
        assertThat(versions).containsExactlyElementsOf(args)
    }

    @Test
    fun testCompare() {
        assertThat(SemVer.parse("1.0.0") < SemVer.parse("1.0.1")).isTrue
        assertThat(SemVer.parse("1.0.0") <= SemVer.parse("1.0.1")).isTrue
        assertThat(SemVer.parse("1.0.1") >= SemVer.parse("1.0.0")).isTrue
        assertThat(SemVer.parse("1.0.1") > SemVer.parse("1.0.0")).isTrue
        assertThat(SemVer.parse("1.0.0") == SemVer.parse("1.0.0")).isTrue
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
        assertThat(v.withNormalVersion(2, 1, 1).toString()).isEqualTo("2.1.1")
        assertThat(v.withPreReleaseVersion(PreReleaseVersion("beta", "1")).toString()).isEqualTo("1.0.0-beta.1")
        assertThat(v.withPreReleaseVersion("beta", "1").toString()).isEqualTo("1.0.0-beta.1")
        assertThat(v.withPreReleaseVersion().hasPreReleaseVersion()).isFalse
        assertThat(v.withBuildMetadata(BuildMetadata("001")).toString()).isEqualTo("1.0.0+001")
        assertThat(v.withBuildMetadata("001").toString()).isEqualTo("1.0.0+001")
        assertThat(v.withBuildMetadata().hasBuildMetadata()).isFalse
        assertThat(v.withPreReleaseVersion(PreReleaseVersion("alpha")).withBuildMetadata(BuildMetadata("001")).toString())
            .isEqualTo("1.0.0-alpha+001")
        val bad = emptyList<Int>()
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVer(NormalVersion(1, 0, bad))
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withNormalVersion(NormalVersion(1, 0, bad))
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withNormalVersion(1, 0, bad)
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withPreReleaseVersion(PreReleaseVersion("beta", bad))
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withPreReleaseVersion("beta", bad)
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withPreReleaseVersion(BuildMetadata("beta", bad))
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withBuildMetadata(BuildMetadata("beta", bad))
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withBuildMetadata("beta", bad)
        }
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            v.withBuildMetadata(PreReleaseVersion("beta", bad))
        }
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
        fun precedenceProvider(): List<List<String>> {
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

        @JvmStatic
        fun createProvider(): List<SemVer> {
            return listOf(
                SemVer(NormalVersion(1, 0, 0)),
                SemVer(NormalVersion("1", "0", "0")),
                SemVer(NormalVersion("1", 0, "0")),
                SemVer(NormalVersion(1, 0, 0), PreReleaseVersion("alpha"), BuildMetadata(1)),
                SemVer(NormalVersion(1, 0, 0), PreReleaseVersion("alpha"), BuildMetadata("1"))
            )
        }
    }
}

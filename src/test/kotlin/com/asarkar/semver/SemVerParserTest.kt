package com.asarkar.semver

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class SemVerParserTest {
    @ParameterizedTest
    @MethodSource("parserTypeProvider")
    fun testParse(parserType: SemVerParserType) {
        val parser = SemVerParser.getInstance(parserType)
        var semVer = parser.parseStr("1.0.0")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isFalse
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)

        semVer = parser.parseStr("1.0.0-alpha")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        var ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("alpha")

        semVer = parser.parseStr("1.0.0-45")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]).isNull()
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("45")

        semVer = parser.parseStr("1.0.0-alpha.1")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("1")
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("alpha")

        semVer = parser.parseStr("1.0.0-0.3.7")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("0", "3", "7")
        Assertions.assertThat(ids[false]).isNull()

        semVer = parser.parseStr("1.0.0-x.7.z.92")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("7", "92")
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("x", "z")

        semVer = parser.parseStr("1.0.0-x-y-z.-")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("x-y-z", "-")

        semVer = parser.parseStr("1.0.0-alpha+001")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isTrue
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("alpha")
        ids = semVer.buildMetadata!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]).isNull()
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("001")

        semVer = parser.parseStr("1.0.0+20130313144700")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isFalse
        Assertions.assertThat(semVer.hasBuildMetadata()).isTrue
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.buildMetadata!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]).isNull()
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("20130313144700")

        semVer = parser.parseStr("1.0.0-beta+exp.sha.5114f85")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isTrue
        Assertions.assertThat(semVer.hasBuildMetadata()).isTrue
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.preReleaseVersion!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("beta")
        ids = semVer.buildMetadata!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("exp", "sha", "5114f85")
        Assertions.assertThat(ids[true]).isNull()

        semVer = parser.parseStr("1.0.0+21AF26D3--117B344092BD")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isFalse
        Assertions.assertThat(semVer.hasBuildMetadata()).isTrue
        Assertions.assertThat(semVer.majorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
        ids = semVer.buildMetadata!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("21AF26D3--117B344092BD")
        Assertions.assertThat(ids[true]).isNull()

        semVer = parser.parseStr("0.1.0")
        Assertions.assertThat(semVer.hasPreReleaseVersion()).isFalse
        Assertions.assertThat(semVer.hasBuildMetadata()).isFalse
        Assertions.assertThat(semVer.majorVersion).isEqualTo(0uL)
        Assertions.assertThat(semVer.minorVersion).isEqualTo(1uL)
        Assertions.assertThat(semVer.patchVersion).isEqualTo(0uL)
    }

    companion object {
        @JvmStatic
        fun parserTypeProvider(): List<SemVerParserType> {
            return SemVerParserType.values().asList()
        }
    }
}

package com.asarkar.semver

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SemVerParserTest {
    @Test
    fun testParse() {
        var semVer = SemVerParser().parse("1.0.0")
        Assertions.assertThat(semVer.hasPreRelease()).isFalse
        Assertions.assertThat(semVer.hasBuild()).isFalse
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)

        semVer = SemVerParser().parse("1.0.0-alpha")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isFalse
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        var ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("alpha")

        semVer = SemVerParser().parse("1.0.0-45")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isFalse
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]).isNull()
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("45")

        semVer = SemVerParser().parse("1.0.0-alpha.1")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isFalse
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("1")
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("alpha")

        semVer = SemVerParser().parse("1.0.0-0.3.7")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isFalse
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("0", "3", "7")
        Assertions.assertThat(ids[false]).isNull()

        semVer = SemVerParser().parse("1.0.0-x.7.z.92")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isFalse
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("7", "92")
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("x", "z")

        semVer = SemVerParser().parse("1.0.0-x-y-z.-")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isFalse
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("x-y-z", "-")

        semVer = SemVerParser().parse("1.0.0-alpha+001")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isTrue
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("alpha")
        ids = semVer.build!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]).isNull()
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("001")

        semVer = SemVerParser().parse("1.0.0+20130313144700")
        Assertions.assertThat(semVer.hasPreRelease()).isFalse
        Assertions.assertThat(semVer.hasBuild()).isTrue
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.build!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]).isNull()
        Assertions.assertThat(ids[true]!!.map { it.toString() }).containsExactly("20130313144700")

        semVer = SemVerParser().parse("1.0.0-beta+exp.sha.5114f85")
        Assertions.assertThat(semVer.hasPreRelease()).isTrue
        Assertions.assertThat(semVer.hasBuild()).isTrue
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.preRelease!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[true]).isNull()
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("beta")
        ids = semVer.build!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("exp", "sha", "5114f85")
        Assertions.assertThat(ids[true]).isNull()

        semVer = SemVerParser().parse("1.0.0+21AF26D3--117B344092BD")
        Assertions.assertThat(semVer.hasPreRelease()).isFalse
        Assertions.assertThat(semVer.hasBuild()).isTrue
        Assertions.assertThat(semVer.major).isEqualTo(1)
        Assertions.assertThat(semVer.minor).isEqualTo(0)
        Assertions.assertThat(semVer.patch).isEqualTo(0)
        ids = semVer.build!!.ids.groupBy { it is NumericId }
        Assertions.assertThat(ids[false]!!.map { it.toString() }).containsExactly("21AF26D3--117B344092BD")
        Assertions.assertThat(ids[true]).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "1.", "1.0", "1.0.", "1.a"])
    fun testInvalidNormal(str: String) {
        assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser().parse(str)
        }
            .withMessage("Invalid normal version")
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.0.0-01", "1.0.0-", "1.0.0-a&^"])
    fun testInvalidPreRelease(str: String) {
        assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser().parse(str)
        }
            .withMessage("Invalid pre-release version")
            .withCauseExactlyInstanceOf(IllegalArgumentException::class.java)
    }
}

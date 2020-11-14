package com.asarkar.semver

fun NormalVersion.withMajorVersion(major: Any): NormalVersion = NormalVersion(NumericId(major, false), minorVersion, patchVersion)
fun NormalVersion.withMinorVersion(minor: Any): NormalVersion = NormalVersion(majorVersion, NumericId(minor, false), patchVersion)
fun NormalVersion.withPatchVersion(patch: Any): NormalVersion = NormalVersion(majorVersion, minorVersion, NumericId(patch, false))

fun SemVer.withMajorVersion(major: Any): SemVer = SemVer(normalVersion.withMajorVersion(major), preReleaseVersion, buildMetadata)
fun SemVer.withMinorVersion(minor: Any): SemVer = SemVer(normalVersion.withMinorVersion(minor), preReleaseVersion, buildMetadata)
fun SemVer.withPatchVersion(patch: Any): SemVer = SemVer(normalVersion.withPatchVersion(patch), preReleaseVersion, buildMetadata)
fun SemVer.withNormalVersion(normalVersion: NormalVersion): SemVer = SemVer(normalVersion, preReleaseVersion, buildMetadata)
fun SemVer.withNormalVersion(major: Any, minor: Any, patch: Any): SemVer = SemVer(NormalVersion(major, minor, patch), preReleaseVersion, buildMetadata)

fun SemVer.withPreReleaseVersion(vararg ids: Any): SemVer {
    return if (ids.isEmpty()) SemVer(normalVersion, null, buildMetadata)
    else if (ids.first() is PreReleaseVersion) SemVer(normalVersion, ids.first() as PreReleaseVersion, buildMetadata)
    else SemVer(normalVersion, PreReleaseVersion(*ids), buildMetadata)
}

fun SemVer.withBuildMetadata(vararg ids: Any): SemVer {
    return if (ids.isEmpty()) SemVer(normalVersion, preReleaseVersion, null)
    else if (ids.first() is BuildMetadata) SemVer(normalVersion, preReleaseVersion, ids.first() as BuildMetadata)
    else SemVer(normalVersion, preReleaseVersion, BuildMetadata(*ids))
}

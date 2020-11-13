package com.asarkar.semver

fun NormalVersion.withMajorVersion(major: Any): NormalVersion = NormalVersion(NumericId(major, false), minorVersion, patchVersion)
fun NormalVersion.withMinorVersion(minor: Any): NormalVersion = NormalVersion(majorVersion, NumericId(minor, false), patchVersion)
fun NormalVersion.withPatchVersion(patch: Any): NormalVersion = NormalVersion(majorVersion, minorVersion, NumericId(patch, false))

fun SemVer.withMajorVersion(major: Any): SemVer = SemVer(normalVersion.withMajorVersion(major), preReleaseVersion, buildMetadata)
fun SemVer.withMinorVersion(minor: Any): SemVer = SemVer(normalVersion.withMinorVersion(minor), preReleaseVersion, buildMetadata)
fun SemVer.withPatchVersion(patch: Any): SemVer = SemVer(normalVersion.withPatchVersion(patch), preReleaseVersion, buildMetadata)
fun SemVer.withNormalVersion(normalVersion: NormalVersion): SemVer = SemVer(normalVersion, preReleaseVersion, buildMetadata)
fun SemVer.withPreReleaseVersion(preReleaseVersion: PreReleaseVersion?): SemVer = SemVer(normalVersion, preReleaseVersion, buildMetadata)
fun SemVer.withBuildMetadata(buildMetadata: BuildMetadata?): SemVer = SemVer(normalVersion, preReleaseVersion, buildMetadata)

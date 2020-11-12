package com.asarkar.semver.antlr

import com.asarkar.semver.AlphanumericId
import com.asarkar.semver.BuildMetadata
import com.asarkar.semver.NormalVersion
import com.asarkar.semver.NumericId
import com.asarkar.semver.PreReleaseVersion
import com.asarkar.semver.SemVer

private val numericIdVisitor = NumericIdVisitor()
private val alphanumericIdVisitor = AlphanumericIdVisitor()

class SemVerVisitorImpl : SemVerBaseVisitor<SemVer>() {
    override fun visitSemVer(ctx: SemVerParser.SemVerContext): SemVer {
        val normal = ctx.normalVersion().accept(NormalVersionVisitor())
        val preRelease = ctx.preReleaseVersion()?.accept(PreReleaseVersionVisitor())
        val build = ctx.buildMetadata()?.accept(BuildMetadataVisitor())
        return SemVer(normal, preRelease, build)
    }
}

class NormalVersionVisitor : SemVerBaseVisitor<NormalVersion>() {
    override fun visitNormalVersion(ctx: SemVerParser.NormalVersionContext): NormalVersion {
        val major = ctx.majorVersion().accept(numericIdVisitor)
        val minor = ctx.minorVersion().accept(numericIdVisitor)
        val patch = ctx.patchVersion().accept(numericIdVisitor)
        return NormalVersion(major, minor, patch)
    }
}

class NumericIdVisitor : SemVerBaseVisitor<NumericId>() {
    override fun visitNumericId(ctx: SemVerParser.NumericIdContext): NumericId {
        return NumericId(ctx.text, false)
    }

    override fun visitDigits(ctx: SemVerParser.DigitsContext): NumericId {
        return NumericId(ctx.text, true)
    }
}

class AlphanumericIdVisitor : SemVerBaseVisitor<AlphanumericId>() {
    override fun visitAlphanumericId(ctx: SemVerParser.AlphanumericIdContext): AlphanumericId {
        return AlphanumericId(ctx.text)
    }
}

class PreReleaseVersionVisitor : SemVerBaseVisitor<PreReleaseVersion>() {
    override fun visitPreReleaseVersion(ctx: SemVerParser.PreReleaseVersionContext): PreReleaseVersion {
        return ctx.preReleaseId().mapNotNull {
            val numId = it.numericId()
            if (numId != null) numId.accept(numericIdVisitor)
            else it.alphanumericId().accept(alphanumericIdVisitor)
        }
            .let { PreReleaseVersion(it) }
    }
}

class BuildMetadataVisitor : SemVerBaseVisitor<BuildMetadata>() {
    override fun visitBuildMetadata(ctx: SemVerParser.BuildMetadataContext): BuildMetadata {
        return ctx.buildMetadataId().mapNotNull {
            val digits = it.digits()
            if (digits != null) digits.accept(numericIdVisitor)
            else it.alphanumericId().accept(alphanumericIdVisitor)
        }
            .let { BuildMetadata(it) }
    }
}

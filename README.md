# jsemver

Kotlin implementation of the [Semantic Versioning specification](https://semver.org/). Can be used from Java seamlessly.

The goal is to strictly comply with the SemVer parsing and precedence rules, and provide an easy-to-use API.
Specifically it provides the ability to:
                                                                                                             
 - [x] Instantiate/parse semantic versions
 - [x] Compare/sort semantic versions
 - [ ] Check if a semantic version fits within a set of constraints
 
 The grammar can be seen [here](src/main/antlr/com/asarkar/semver/antlr/SemVer.g4); it is consistent with the 
[BNF grammar](https://semver.org/#backusnaur-form-grammar-for-valid-semver-versions) in the specification.

[![](https://github.com/asarkar/jsemver/workflows/CI%20Pipeline/badge.svg)](https://github.com/asarkar/jsemver/actions?query=workflow%3A%22CI+Pipeline%22)

## Installation

You can find the latest version on Bintray. [ ![Download](https://api.bintray.com/packages/asarkar/mvn/com.asarkar%3Ajsemver/images/download.svg) ](https://bintray.com/asarkar/mvn/com.asarkar%3Ajsemver/_latestVersion)

I'll consider publishing to Maven Central and jcenter if people start using this library and find it useful.

## Usage

### Parse semantic versions

A valid semantic version is represented by the `SemVer` class. The easiest way to instantiate it is by parsing a string:
```
SemVer.parseStr("1.0.0")
```

`SemVer` comprises `NormalVersion`, and optionally, `PreReleaseVersion` and `BuildMetadata`. The objects are immutable but there are 
various builder methods that return new instances after appropriate modification.

More ways to instantiate a `SemVer`:
```
SemVer(NormalVersion(1, 0, 0))
SemVer(NormalVersion("1", "0", "0"))
SemVer(NormalVersion(1, 0, 0), PreReleaseVersion("alpha"), BuildMetadata(1))
v
  .withMajorVersion(2).withMinorVersion(1).withPatchVersion(1)
  .withPreReleaseVersion("alpha").withBuildMetadata(1)
```

`v.withPreReleaseVersion()` (empty) will remove the pre-release version. Same for `v.withBuildMetadata()`.

See the KDoc for more details. This project has almost 100% test coverage, you're also welcome to look at the unit tests.

### Compare/sort semantic versions

Other than `BuildMetadata`, the rest implement `equals`, `hashCode`, and `Comparable`.

From Kotlin code, you can use the usual comparison operators:
```
SemVer.parse("1.0.0") < SemVer.parse("1.0.1")
SemVer.parse("1.0.1") >= SemVer.parse("1.0.0")
```
From Java, use `compareTo` explicitly.

### Check if a semantic version fits within a set of constraints

I don't see the point in providing a DSL for checking complicated constraints, 
because all of those can be rewritten using `compareTo`. For example:
```
v.satisfies(">1.2.2") can be rewritten as v > SemVer.parse("1.2.2")
v.satisfies("1.2.+") can be rewritten as v >= SemVer.parse("1.2.0")
v.satisfies("(,1.8.9]") can be rewritten as v <= SemVer.parse("1.8.9")
v.satisfies("1.0-2.0") can be rewritten as v >= SemVer.parse("1.0.0") && v <= SemVer.parse("2.0.0")
``` 

See [Semver cheatsheet](https://devhints.io/semver) for more examples.

## Contribute

This project is a volunteer effort. You are welcome to send pull requests, ask questions, or create issues.
If you like it, you can help by spreading the word!

## License

Copyright 2020 Abhijit Sarkar - Released under [Apache License v2.0](LICENSE).

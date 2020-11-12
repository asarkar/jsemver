# jsemver

Kotlin implementation of the [Semantic Versioning specification](https://semver.org/). Can be used from Java seamlessly.

The goal is to strictly comply with the SemVer parsing and precedence rules, and provide an easy-to-use API. There isn't, 
and probably never will be, a boatload of methods to determine whether version `A` is compatible with version `B`. It's
not up to the library to determine that; however, the specification requires two versions with the same major version 
to be API compatible, so you can easily come to your own conclusion.

The specification doesn't support wildcard versions, neither does this library. Wildcard versions are a build tool thing.

[![](https://github.com/asarkar/jsemver/workflows/CI%20Pipeline/badge.svg)](https://github.com/asarkar/jsemver/actions?query=workflow%3A%22CI+Pipeline%22)

## Installation

You can find the latest version on Bintray. [ ![Download](https://api.bintray.com/packages/asarkar/mvn/com.asarkar%3Ajsemver/images/download.svg) ](https://bintray.com/asarkar/mvn/com.asarkar%3Ajsemver/_latestVersion)

I'll consider publishing to Maven Central and jcenter if people start using this library and find it useful.

## Usage

A valid semantic version is represented by the `SemVer` class. The easiest way to instantiate it is by parsing a string:
```
val v = SemVer.parseStr("1.0.0")
```

`SemVer` comprises `NormalVersion`, and optionally, `PreRelease` and `Build`. The objects are immutable but there are 
various builder methods that return new instances after appropriate modification. Other than `Build`, the rest implement
`equals`, `hashCode`, and `Comparable`.

See the KDoc for more details. This project has almost 100% test coverage, you're also welcome to look at the unit tests.


## Contribute

This project is a volunteer effort. You are welcome to send pull requests, ask questions, or create issues.
If you like it, you can help by spreading the word!

## License

Copyright 2020 Abhijit Sarkar - Released under [Apache License v2.0](LICENSE).

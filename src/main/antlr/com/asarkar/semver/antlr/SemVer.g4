grammar SemVer;

@header {
package com.asarkar.semver.antlr;
}

semVer : normalVersion (HYPHEN preReleaseVersion)? (PLUS buildMetadata)? ;
normalVersion : majorVersion DOT minorVersion DOT patchVersion ;
majorVersion : numericId ;
minorVersion : numericId ;
patchVersion : numericId ;
preReleaseVersion : preReleaseId (DOT preReleaseId)* ;
buildMetadata : buildMetadataId (DOT buildMetadataId)* ;
preReleaseId : alphanumericId
             | numericId
             ;
buildMetadataId : alphanumericId
                | digits
                ;
alphanumericId : (characters)? nonDigit (characters)? ;
numericId : ZERO
          | POSITIVE_DIGIT (digits)?
          ;
characters : character+ ;
character : digit
          | nonDigit
          ;
digits: digit+ ;
nonDigit : LETTER
         | HYPHEN
         ;
digit : ZERO
      | POSITIVE_DIGIT
      ;
ZERO : '0' ;
HYPHEN : '-' ;
DOT : '.' ;
PLUS : '+' ;
POSITIVE_DIGIT : [1-9] ;
LETTER : [a-zA-Z] ;

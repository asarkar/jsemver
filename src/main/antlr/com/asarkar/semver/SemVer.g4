grammar SemVer;

@header {
package com.asarkar.semver;
}

semVer : normal ('-' preRelease)? ('+' build)? ;
normal : major '.' minor '.' patch ;
major : NUM ;
minor : NUM ;
patch : NUM ;
preRelease : PRE_RELEASE ('.' preRelease)* ;
build : BUILD ('.' build)*;
NUM : '0'
    | POSITIVE_DIGIT
    | POSITIVE_DIGIT DIGITS
    ;
BUILD : ALPHANUM
      | DIGITS
      ;
PRE_RELEASE : ALPHANUM
            | NUM
            ;
fragment
ALPHANUM : NON_DIGIT
         | NON_DIGIT CHARS
         | CHARS NON_DIGIT
         | CHARS NON_DIGIT CHARS
         ;
fragment
CHARS : CHAR+ ;
fragment
CHAR : DIGIT
     | NON_DIGIT
     ;
fragment
NON_DIGIT : LETTER
          | '-'
          ;
fragment
DIGITS : DIGIT+ ;
fragment
DIGIT : '0'
      | POSITIVE_DIGIT
      ;
fragment
POSITIVE_DIGIT : [1-9] ;
fragment
LETTER : [a-zA-Z] ;

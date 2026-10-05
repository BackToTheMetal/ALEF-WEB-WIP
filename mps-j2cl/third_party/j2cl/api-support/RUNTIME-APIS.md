The UUID implementation preserves the MPS-used constructor, field access,
parsing, formatting, equality, hash, ordering, and randomUUID behavior. It also
provides version, variant, timestamp, clockSequence, and node. Parsing preserves
Java 21's accepted short fields, masking, optional plus signs, and BMP hexadecimal
digit forms; canonical output is lowercase ASCII. The unused MD5-based
nameUUIDFromBytes API is not implemented.

SecureRandom obtains every random byte through browser Web Crypto
getRandomValues, in requests of at most 65536 bytes. It overrides all random
generators present in the pinned J2CL Random; overriding protected next alone
would leave that release's public methods using its private LCG helper.
Cryptographic entropy is required; there is no Math.random fallback. The browser
owns entropy collection and seeding. Provider selection and seeded constructors
are not implemented; explicit nonzero long seeding and byte-array seeding fail
with UnsupportedOperationException instead of claiming to affect browser entropy.

Character classification and simple case mappings are generated centrally from
Java 21.0.12. The char and int overloads of isDigit, isLetter, isLetterOrDigit,
isLowerCase, isUpperCase, isTitleCase, isWhitespace, isSpaceChar,
isJavaIdentifierStart, isJavaIdentifierPart, toLowerCase,
toUpperCase, toTitleCase, and digit use those tables. Char overloads retain UTF-16
code-unit semantics; int overloads handle supplementary and invalid code points
like Java 21. The existing JVM-derived OpenAPI identifier dataset is independent.
Regenerate compatibility/java-characters.json by compiling/running
tools/JavaCharacterData.java under Java 21, then run tools/make_character_data.py.

The kernel additions include the Java LinkageError/NoClassDefFoundError and
VirtualMachineError hierarchies with message/cause constructors. An empty
enumeration has no elements and preserves Java 21's empty asIterator behavior.
System.lineSeparator caches the line.separator build-time define, defaulting
to LF for browser text. tools/prepare_kernel_jre.py reproduces the Collections
and System source additions independently of the root's other API generators.

tests/runtime_support runs the same UUID, reflection-array, and SecureRandom
checks on the JVM and browser. The UUID implementation itself is additionally
compiled into an isolated JVM java.base patch for differential validation against
the unmodified JVM oracle. Browser tests must also exercise the actual Web Crypto
provider, including requests exceeding its per-call quota.

Reproduce the JVM comparison with Java 21:

```sh
python3 tools/run_runtime_oracle.py --check
```

The generated Character helper is compared exhaustively with Java 21 over all
1114112 Unicode code points, invalid int values, char mappings, and radices 0–37.
The browser fixture exercises every boundary from every generated Character table.
The parser comparison covers 132782 inputs, including every UTF-16 character
in two field positions and malformed field lengths/signs. It compares both
canonical output and exception class against the stock JVM.

Sources:

- [Java 21 UUID API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/UUID.html)
- [Java 21 SecureRandom API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/SecureRandom.html)
- [Web Crypto getRandomValues](https://www.w3.org/TR/webcrypto/#Crypto-method-getRandomValues)

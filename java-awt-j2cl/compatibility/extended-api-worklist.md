# Extended API finalization worklist

This audit includes protected members, accessible nested types, and the shipped prerequisite type families. It extends the older public-only AWT inventory. Missing candidates need implementation or an explicit reviewed profile disposition; they are not automatically classified as supported or unavailable. Declaration differences require source-compatibility review and consumer compilation probes.

| Metric | Count |
| --- | ---: |
| referenceTypeCount | 441 |
| emittedTypeCount | 397 |
| unreviewedTypeCount | 0 |
| unreviewedMemberCount | 0 |
| missingCandidateTypeCount | 0 |
| missingCandidateMemberCount | 0 |
| typeDeclarationReviewCount | 0 |
| memberDeclarationReviewCount | 0 |
| missingSupportedTypeCount | 0 |
| missingSupportedMemberCount | 0 |
| supportedTypeDeclarationFailureCount | 0 |
| supportedMemberDeclarationFailureCount | 0 |
| reviewedMemberDeclarationFailureCount | 0 |
| releaseReady | True |

The API-profile completion gate requires all reviews and retained-contract gaps to be closed. Run `audit_extended_api.py --require-complete` to enforce it. A passing API gate does not complete the manual acceptance and release-engineering work in FINALIZATION.md.

## Work queue

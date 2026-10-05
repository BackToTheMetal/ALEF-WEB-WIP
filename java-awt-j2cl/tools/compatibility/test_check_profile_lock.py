import copy
import unittest

from check_profile_lock import profile_changes, profile_snapshot


class ProfileLockTest(unittest.TestCase):
    def setUp(self):
        self.manifest = {"entries": [{"type": "java.awt.Example", "supportStatus": "limited", "implementationState": "partial", "members": [{"signature": "public void first();", "supportStatus": "limited"}, {"signature": "public void second();", "supportStatus": "unavailable"}]}]}
        self.locked = profile_snapshot(self.manifest)

    def test_reclassification_cannot_hide_missing_member(self):
        self.manifest["entries"][0]["members"][0]["supportStatus"] = "unavailable"
        self.assertTrue(profile_changes(self.locked, profile_snapshot(self.manifest)))

    def test_swapping_dispositions_with_same_counts_is_detected(self):
        members = self.manifest["entries"][0]["members"]
        members[0]["supportStatus"], members[1]["supportStatus"] = members[1]["supportStatus"], members[0]["supportStatus"]
        current = profile_snapshot(self.manifest)
        self.assertEqual(self.locked["types"]["java.awt.Example"]["memberCounts"], current["types"]["java.awt.Example"]["memberCounts"])
        self.assertTrue(profile_changes(self.locked, current))

    def test_removed_type_or_signature_is_detected(self):
        removed_type = copy.deepcopy(self.manifest)
        removed_type["entries"].clear()
        self.assertTrue(profile_changes(self.locked, profile_snapshot(removed_type)))
        self.manifest["entries"][0]["members"].pop()
        self.assertTrue(profile_changes(self.locked, profile_snapshot(self.manifest)))

    def test_progress_and_order_do_not_change_profile(self):
        self.manifest["entries"][0]["implementationState"] = "implemented"
        self.manifest["entries"][0]["members"].reverse()
        self.assertEqual([], profile_changes(self.locked, profile_snapshot(self.manifest)))

    def test_duplicates_and_unresolved_dispositions_are_rejected(self):
        entry = self.manifest["entries"][0]
        self.manifest["entries"].append(copy.deepcopy(entry))
        with self.assertRaisesRegex(ValueError, "Duplicate profile type"):
            profile_snapshot(self.manifest)
        self.manifest["entries"].pop()
        entry["members"][0]["supportStatus"] = "deferred"
        with self.assertRaisesRegex(ValueError, "Unresolved profile member"):
            profile_snapshot(self.manifest)

    def test_reviewed_hook_and_prerequisite_removal_require_profile_update(self):
        extensions = {"types": [], "members": [{"type": "java.awt.Example", "signature": "protected void dispatch();", "supportStatus": "limited"}]}
        prerequisites = {"types": ["java.net.URI"]}
        locked = profile_snapshot(self.manifest, extensions, prerequisites)
        extensions["members"][0]["supportStatus"] = "unavailable"
        self.assertIn("Changed reviewed extended API dispositions", profile_changes(locked, profile_snapshot(self.manifest, extensions, prerequisites)))
        prerequisites["types"].clear()
        self.assertIn("Changed prerequisite reference type families", profile_changes(locked, profile_snapshot(self.manifest, extensions, prerequisites)))


if __name__ == "__main__":
    unittest.main()

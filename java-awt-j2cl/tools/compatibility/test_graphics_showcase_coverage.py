import copy
import json
import unittest

from graphics_showcase_coverage import ROOT, build_report, inventory, key, validate_reviews


class GraphicsShowcaseCoverageTest(unittest.TestCase):
    def setUp(self):
        self.reference = json.loads((ROOT / "compatibility/openjdk-awt-api.json").read_text())
        self.reviews = json.loads((ROOT / "compatibility/graphics2d-showcase-reviews.json").read_text())["reviews"]

    def test_keeps_overloads_and_deduplicates_overrides(self):
        methods = inventory(self.reference)
        self.assertIn("translate(int,int)", methods)
        self.assertIn("translate(double,double)", methods)
        self.assertEqual(8, sum(method.startswith("drawImage(") for method in methods))
        self.assertEqual("java.awt.Graphics2D", methods["translate(int,int)"]["declaredBy"])
        self.assertEqual(key("getRenderingHint(java.awt.RenderingHints$Key)"), key("getRenderingHint(RenderingHints.Key)"))
        self.assertEqual("setRenderingHints(Map)", key("setRenderingHints(java.util.Map<?, ?>)"))

    def test_resolves_image_overloads_to_their_actual_owner(self):
        rows = {row["method"]: row for row in build_report(self.reference, self.reviews)["methods"]}
        self.assertTrue(rows["drawImage(Image,int,int,ImageObserver)"]["backends"]["Canvas"]["implementation"].endswith("/Graphics.java"))
        self.assertTrue(rows["drawImage(BufferedImage,BufferedImageOp,int,int)"]["backends"]["Canvas"]["implementation"].endswith("/CanvasGraphics2D.java"))
        unreviewed = {row["method"]: row for row in build_report(self.reference, [])["methods"]}
        self.assertEqual("pending", unreviewed["drawImage(Image,int,int,ImageObserver)"]["backends"]["Canvas"]["coverage"])
        self.assertEqual("unavailable", rows["setXORMode(Color)"]["backends"]["Canvas"]["coverage"])
        self.assertTrue(build_report(self.reference, self.reviews)["complete"])
        self.assertFalse(build_report(self.reference, self.reviews[:-1])["complete"])

    def test_rejects_unknown_and_duplicate_reviews(self):
        for method in ["notAnApi()", self.reviews[0]["method"]]:
            reviews = copy.deepcopy(self.reviews)
            extra = copy.deepcopy(reviews[0])
            extra["method"] = method
            reviews.append(extra)
            with self.assertRaises(ValueError):
                validate_reviews(reviews, inventory(self.reference), ROOT)

    def test_requires_evidence_for_each_backend_and_an_observable(self):
        for field in ["Canvas", "BufferedImage", "observable"]:
            reviews = copy.deepcopy(self.reviews)
            if field == "observable":
                reviews[0][field] = ""
            else:
                reviews[0]["evidence"][field] = ["missing-evidence.java"]
            with self.assertRaises(ValueError):
                validate_reviews(reviews, inventory(self.reference), ROOT)


if __name__ == "__main__":
    unittest.main()

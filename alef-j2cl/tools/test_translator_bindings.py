#!/usr/bin/env python3
"""Regression checks for argument splitting in generated typed dispatch."""
import unittest
from adapt_translators import params


class TranslatorArguments(unittest.TestCase):
    def test_nested_generic_aggregation(self):
        self.assertEqual(params('AGGREGATIE self, SNode node, Iterable<Tuples._2<SNode, Iterable<Object>>> values'),
                         ['AGGREGATIE self', 'SNode node', 'Iterable<Tuples._2<SNode, Iterable<Object>>> values'])

    def test_constructor_maps(self):
        self.assertEqual(params('TranslatorScope scope, Map<MemoKey, Object> local, Map<MemoKey, Object> shared'),
                         ['TranslatorScope scope', 'Map<MemoKey, Object> local', 'Map<MemoKey, Object> shared'])

    def test_empty(self):
        self.assertEqual(params(''), [])


if __name__ == '__main__':
    unittest.main()

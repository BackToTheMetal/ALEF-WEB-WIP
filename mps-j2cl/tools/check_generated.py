#!/usr/bin/env python3
"""Verify generated build metadata, source overlays, JRE patches and source ownership."""
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
commands = [
    ['inventory_mps.py'],
    ['inventory_browser.py'],
    ['compose_jre.py', '--upstream', '.work/j2cl-20260402'],
    ['make_type_support_patch.py', '--upstream', '.work/j2cl-20260402'],
    ['make_classloader_patch.py'],
    ['make_character_data.py'],
    ['make_regex_unicode_data.py'],
    ['prepare_api_basics.py'],
    ['prepare_kernel_jre.py'],
    ['make_api_patch.py'],
    ['prepare_dependency_views.py'],
    ['prepare_editor_runtime.py'],
    ['prepare_editor_shortcuts.py'],
    ['prepare_editor_companions.py'],
    ['prepare_editor_assets.py'],
    ['prepare_action_dispatcher.py'],
    ['inventory_intellij.py'],
    ['prepare_openapi.py'],
    ['prepare_kernel_platform.py'],
    ['prepare_kernel_utilities.py'],
    ['prepare_module_reference.py'],
    ['prepare_runtime_flags.py'],
    ['prepare_pattern_util.py'],
    ['make_vfs_patch.py'],
    ['make_dynamic_components_patch.py'],
    ['check_sources.py', '--report', 'out/source-audit.json'],
]
for name, *args in commands:
    subprocess.run([sys.executable, str(ROOT / 'tools' / name), *args, '--check'], cwd=ROOT, check=True)
print('Generated metadata, patches and complete source ownership: PASS')

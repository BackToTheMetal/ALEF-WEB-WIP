"""Extract pristine generated Java from the pinned upstream distribution."""
from pathlib import Path
from zipfile import ZipFile
from io import BytesIO
import hashlib
import json

ROOT = Path(__file__).resolve().parents[1]
archive = ROOT / '.work/extensions.zip'
lock = json.loads((ROOT / 'source-lock.json').read_text())
assert hashlib.sha256(archive.read_bytes()).hexdigest() == lock['extensions']['source_archive_sha256'], 'Unreviewed source archive'
records = []
with ZipFile(archive) as distribution:
    for jar in distribution.namelist():
        if not jar.endswith('-src.jar'):
            continue
        module = Path(jar).name[:-8]
        with ZipFile(BytesIO(distribution.read(jar))) as sources:
            for name in sources.namelist():
                if not name.endswith('.java'):
                    continue
                path = Path(name)
                if path.is_absolute() or '..' in path.parts:
                    raise ValueError(name)
                content = sources.read(name)
                target = ROOT / 'upstream-src' / module / path
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes(content)
                records.append({'path': target.relative_to(ROOT).as_posix(), 'sha256': hashlib.sha256(content).hexdigest(), 'jar': jar})
(ROOT / 'upstream-sources.json').write_text(json.dumps(records, indent=2) + '\n')
print(f'Extracted {len(records)} pristine Java sources; archive SHA256 {hashlib.sha256(archive.read_bytes()).hexdigest()}')

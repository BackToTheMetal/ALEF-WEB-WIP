#!/usr/bin/env python3
"""Build upstream locale processors, generate metadata, and assemble a J2CL overlay."""
from pathlib import Path
import os, subprocess, urllib.request, shutil, json, re, hashlib

ROOT = Path(__file__).resolve().parents[1]
UP = ROOT / 'upstream'
OUT = ROOT / 'out'

def run(args):
    subprocess.run([str(x) for x in args], check=True, cwd=ROOT)

def main():
    OUT.mkdir(exist_ok=True)
    jars = OUT / 'jars'
    jars.mkdir(exist_ok=True)
    jar = jars / 'gwt-internal-annotations-v20230718-1.jar'
    if not jar.exists():
        urllib.request.urlretrieve('https://repo.maven.apache.org/maven2/com/vertispan/j2cl/gwt-internal-annotations/v20230718-1/' + jar.name, jar)
    if hashlib.sha256(jar.read_bytes()).hexdigest() != 'ec18fc3e264d6cd341e1b78230c36a969fbb2e9dd96e7782bcdde9197fd104c8':
        raise ValueError('Annotation jar checksum mismatch')
    classes = OUT / 'processors'
    generated = ROOT / 'generated'
    if classes.exists():
        shutil.rmtree(classes)
    if generated.exists():
        shutil.rmtree(generated)
    classes.mkdir(exist_ok=True)
    generated.mkdir(exist_ok=True)
    roots = [x / 'src/main/java' for x in sorted(UP.iterdir()) if (x / 'src/main/java').exists()]
    sourcepath = os.pathsep.join(map(str, roots + [generated]))
    cp = os.pathsep.join(map(str, [classes, jar]))
    locale = UP / 'j2cl-java-util-Locale-annotation-processor/src/main/java/walkingkooka/j2cl/java/util/locale/annotationprocessor/LocaleProviderAnnotationProcessor.java'
    processors = sorted(p for root in roots for p in root.rglob('*AnnotationProcessor.java') if p.name != 'LocaleAwareAnnotationProcessor.java')
    for root in roots:
        for p in root.rglob('*'):
            if p.is_file() and p.suffix != '.java':
                dest = classes / p.relative_to(root)
                dest.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(p, dest)
    trigger = OUT / 'Generate.java'
    trigger.write_text('@walkingkooka.j2cl.locale.LocaleAware public class Generate {}\n')
    profile = json.loads((ROOT / 'profile.json').read_text())
    options = ['-Awalkingkooka.j2cl.java.util.' + key + '=' + profile[value] for key, value in {
        'Locale': 'locales', 'Locale.DEFAULT': 'defaultLocale', 'Currency': 'currencies',
        'TimeZone': 'timeZones', 'TimeZone.DEFAULT': 'defaultTimeZone'}.items()]
    options.append('-Awalkingkooka.j2cl.locale.Logging=NONE')
    for group in [[locale], processors]:
        run(['javac', '-J-Xmx768m', '-proc:none', '-cp', cp, '-sourcepath', sourcepath, '-d', classes, UP / 'walkingkooka/src/main/java/walkingkooka/predicate/ComparatorComparisonRelationPredicate.java', *group])
        names = [re.search(r'package ([\w.]+);', p.read_text()).group(1) + '.' + p.stem for p in group]
        run(['javac', '-J-Xmx768m', '-proc:only', '-cp', cp, '-sourcepath', sourcepath, '-processor', ','.join(names), '-s', generated, '-d', classes, *options, trigger])
    print('Generated locale providers.', flush=True)
    tzdb = Path(shutil.which('javac')).resolve().parents[1] / 'lib/tzdb.dat'
    data = tzdb.read_bytes()
    # TZDB binary header: format byte, UTF group, short version count, UTF version.
    group_length = int.from_bytes(data[1:3], 'big')
    version_start = 3 + group_length + 2
    version_length = int.from_bytes(data[version_start:version_start + 2], 'big')
    version = data[version_start + 2:version_start + 2 + version_length].decode()
    (ROOT / 'release').mkdir(exist_ok=True)
    (ROOT / 'release/metadata.json').write_text(json.dumps({
        'profile': profile,
        'java': subprocess.check_output(['java', '-version'], stderr=subprocess.STDOUT, text=True).strip(),
        'tzdbVersion': version, 'tzdbSha256': hashlib.sha256(data).hexdigest(),
        'providers': {p.relative_to(generated).as_posix(): hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(generated.rglob('*.java'))}
    }, indent=2) + '\n')

if __name__ == '__main__':
    main()

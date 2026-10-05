#!/usr/bin/env python3
"""Generate in a task-owned WSL cache; never write into the ALEF checkout."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import tarfile
import urllib.request
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
LOCK = json.loads((ROOT / 'source-lock.json').read_text())
NS = {'m': 'http://maven.apache.org/POM/4.0.0'}


def run(args, cwd, env=None, log=None):
    print('+', ' '.join(map(str, args)), flush=True)
    if log:
        log.parent.mkdir(parents=True, exist_ok=True)
        with log.open('w') as out:
            result = subprocess.run(args, cwd=cwd, env=env, stdout=out, stderr=subprocess.STDOUT)
        if result.returncode:
            print('\n'.join(log.read_text(errors='replace').splitlines()[-70:]), flush=True)
        result.check_returncode()
    else:
        subprocess.run(args, cwd=cwd, env=env, check=True)


def prepare(cache):
    commit = LOCK['alef']['commit']
    head = subprocess.check_output(['git', '-C', str(ROOT / 'alef'), 'rev-parse', 'HEAD'], text=True).strip()
    if head != commit:
        raise RuntimeError('ALEF checkout does not match source-lock.json')
    source = cache / 'alef'
    if not source.exists():
        source.mkdir(parents=True)
        archive = cache / 'alef.tar'
        run(['git', '-C', str(ROOT / 'alef'), 'archive', '--output', str(archive), commit], ROOT)
        with tarfile.open(archive) as tf:
            tf.extractall(source, filter='data')
        # Generation needs the Linux JVM only, not distributable JVMs for every OS.
        # This is disposable generation metadata, never a change to upstream ALEF.
        pom = source / 'alef-plugin-pom.xml'
        tree = ET.parse(pom)
        dependencies = tree.getroot().find('m:dependencies', NS)
        for dep in list(dependencies):
            if dep.findtext('m:artifactId', namespaces=NS) == 'jbr' and dep.findtext('m:classifier', namespaces=NS) != 'linux-x64':
                dependencies.remove(dep)
        for executions in tree.findall('.//m:executions', NS):
            for execution in list(executions):
                if execution.findtext('m:id', namespaces=NS) == 'copy-jbrs':
                    executions.remove(execution)
        ET.register_namespace('', NS['m'])
        tree.write(pom, encoding='utf-8', xml_declaration=True)
        (cache / 'revision').write_text(commit + '\n')
    if (cache / 'revision').read_text().strip() != commit:
        raise RuntimeError('Generation cache belongs to a different revision')
    version = LOCK['generation']['maven']
    maven = cache / ('apache-maven-' + version)
    if not maven.exists():
        name = f'apache-maven-{version}-bin.tar.gz'
        url = f'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/{version}/{name}'
        archive = cache / name
        urllib.request.urlretrieve(url, archive)
        checksum = urllib.request.urlopen(url + '.sha512').read().decode().strip().split()[0]
        if hashlib.sha512(archive.read_bytes()).hexdigest() != checksum:
            raise RuntimeError('Maven checksum mismatch')
        with tarfile.open(archive) as tf:
            tf.extractall(cache, filter='data')
    work = ROOT / '.work'
    work.mkdir(exist_ok=True)
    link = work / 'generation'
    if not link.exists():
        link.symlink_to(cache, target_is_directory=True)
    elif link.resolve() != cache:
        raise RuntimeError('Existing generation link points to a different cache')
    return source, maven / 'bin/mvn'


def collect(source):
    entries = []
    for directory in sorted(source.rglob('source_gen')):
        if 'target' in directory.relative_to(source).parts or not directory.is_dir():
            continue
        for path in sorted(directory.rglob('*')):
            if not path.is_file() or path.suffix not in ('.java', '.xml', '.properties'):
                continue
            relative = path.relative_to(source)
            target = ROOT / 'upstream-src' / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(path, target)
            entries.append({'path': target.relative_to(ROOT).as_posix(), 'sha256': hashlib.sha256(path.read_bytes()).hexdigest()})
    report = {'alef_commit': LOCK['alef']['commit'], 'generation': LOCK['generation'], 'sources': entries}
    (ROOT / 'out').mkdir(exist_ok=True)
    (ROOT / 'out/generated-sources.json').write_text(json.dumps(report, indent=2) + '\n')
    print(f'Collected {len(entries)} generated files', flush=True)


def generate(source, cache):
    version = (source / 'version.txt').read_text().strip()
    components = version.split('.')
    mps = source / ('target/mps-' + LOCK['generation']['mps'])
    jbr = next((source / 'target').glob('jbr-*-linux-x64'))
    # ZIP extraction does not reliably retain the executable bit of bundled tools.
    for path in (jbr / 'bin').iterdir():
        if path.is_file():
            path.chmod(path.stat().st_mode | 0o111)
    env = dict(os.environ, MPS_HOME=str(mps), MPS_JDK=str(jbr),
               BRANCH_NAME='develop', GIT_COMMIT=LOCK['alef']['commit'],
               JAVA_VERSION='21', MPS_BUILD_NUMBER=(mps / 'build.txt').read_text().strip(),
               VERSION_NUMBER=version, VERSION_NUMBER_MAJOR=components[0],
               VERSION_NUMBER_MINOR=components[1], VERSION_NUMBER_BUGFIXNR=components[2],
               BUILD_NUMBER='0', GIT_URL=LOCK['alef']['url'], PROJECT_VERSION='0.0.0')
    steps = [
        ('plugins/build-extensions/build_build_extensions.xml', ['generate', 'assemble'], None),
        ('plugins/build-extensions/build-extensions-mpsplugin.xml', ['generate', 'assemble'], 'plugins/build-extensions/build/artifacts/build-extensions-plugin'),
        ('build_build.xml', ['generate', 'assemble'], None),
        ('plugins/json/build-json-plugin.xml', ['generate', 'assemble'], None),
        ('plugins/projecthygiene/hygiene-mpsplugin.xml', ['generate', 'assemble'], 'plugins/projecthygiene/build/artifacts/hygiene-plugin'),
        ('plugins/translator/translator-mpsplugin.xml', ['generate', 'assemble'], 'plugins/translator/build/artifacts/translator-plugin'),
        ('plugins/linguistics/linguistics-mpsplugin.xml', ['generate', 'assemble'], 'plugins/linguistics/build/artifacts/linguistics-plugin'),
        ('alef-plugin.xml', ['generate'], None),
    ]
    stamps = cache / 'generation-stamps'
    stamps.mkdir(exist_ok=True)
    for index, (xml, targets, plugin) in enumerate(steps):
        stamp = stamps / (Path(xml).stem + '.done')
        if stamp.exists():
            continue
        run(['xvfb-run', '-a', 'bash', 'scripts/doAnt.sh', '-f', xml, *targets], source, env,
            ROOT / f'out/generate-{index:02d}-{Path(xml).stem}.log')
        if plugin:
            shutil.copytree(source / plugin, source / 'target/extra-mps-plugins', dirs_exist_ok=True)
        stamp.write_text(LOCK['alef']['commit'] + '\n')
    collect(source)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--stage', choices=['prepare', 'dependencies', 'generate', 'collect'], default='dependencies')
    parser.add_argument('--cache', type=Path, default=Path.home() / '.cache/alef-j2cl' / LOCK['alef']['commit'][:12])
    args = parser.parse_args()
    cache = args.cache.resolve()
    cache.mkdir(parents=True, exist_ok=True)
    source, mvn = prepare(cache)
    if args.stage == 'dependencies':
        env = dict(os.environ, JAVA_HOME='/usr/lib/jvm/java-21-openjdk-amd64')
        env['PATH'] = str(mvn.parent) + ':' + env['PATH']
        run([str(mvn), '-B', '--no-transfer-progress', '-Pinstall-deps', '-Dmaven.test.skip=true',
             '-Dmaven.repo.local=' + str(cache / 'maven-repository'), 'package'], source, env, ROOT / 'out/generation-dependencies.log')
    elif args.stage == 'collect':
        collect(source)
    elif args.stage == 'generate':
        generate(source, cache)
    print('Generation workspace:', source, flush=True)


if __name__ == '__main__':
    main()

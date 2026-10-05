# ALEF technical paper

Implementation snapshot: 29 September 2026. Covers the integrated application,
including package change removal, with ten vector diagrams and a risk assessment.

- Editable source: [ALEF-technical-paper.md](ALEF-technical-paper.md)
- PDF: [alef-technical-paper.pdf](../../output/pdf/alef-technical-paper.pdf)
- Diagram source and PDF generation: [build_paper.py](build_paper.py)
- Inspected source paths and hashes: [source-index.json](source-index.json)
- PDF hash and section/page index: [build-report.json](build-report.json)

From the frontend repository root, run `python docs/technical-paper/build_paper.py`
using Python with ReportLab and the listed Windows fonts installed. The builder
also reads the sibling ALEFBackend sources to regenerate the source index; it
does not change the application, database, workspace, packages or deployments.

After editing, regenerate the PDF and render every page with Poppler for visual
inspection. Revisit evidence dates and implementation-status statements whenever
the underlying code changes. Do not treat retained test results as new test runs.

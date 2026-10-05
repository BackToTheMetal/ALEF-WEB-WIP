# Sportclub: van gegevens naar een geteste service

Een klein, fictief voorbeeld dat de volledige keten laat zien: objectmodel →
regels → service → servicetests. De bedragen zijn voorbeeldtarieven.

## Open deze zes roots in de workspace

1. **Sportclub** — het objecttype **Deelnemer**, met `naam` (tekst), `leeftijd`
   (geheel getal) en `jaarcontributie` (bedrag met twee decimalen).
2. **Contributieregels** — twee elkaar uitsluitende regels:
   **Jeugdcontributie** kent €75 toe bij een leeftijd onder 18 jaar;
   **Volwassenencontributie** kent €125 toe vanaf 18 jaar.
3. **Contributieservice** — het entrypoint **BerekenContributie** ontvangt de
   naam en leeftijd van één deelnemer, voert de regels uit en geeft diens
   jaarcontributie terug. De service verwijst naar de afzonderlijke mappings.
4. **Deelnemergegevens** — de invoermapping voor naam en leeftijd, als eigen
   bestand in hetzelfde Service-model als Contributieservice.
5. **Contributieberekening** — de uitvoermapping voor jaarcontributie, als eigen
   bestand naast de service en invoermapping.
6. **Contributie controleren** — vijf echte Testspraak-servicetests, inclusief
   de grensgevallen van 17 en 18 jaar.

| Deelnemer | Leeftijd | Verwachte jaarcontributie |
|---|---:|---:|
| Noor | 10 | €75 |
| Sam | 17 | €75 |
| Mila | 18 | €125 |
| Daan | 35 | €125 |
| Ada | 70 | €125 |

De tests controleren ook een succesvolle service-uitvoering (`SERVICE_OK`) en
serviceversie `1.0.0`. De regels gelden voor de niet-negatieve leeftijden in dit
voorbeeld; invoervalidatie voor ongeldige leeftijden is geen afzonderlijke regel.

## Bewaren en controleren

`workspace.json` bevat alle voorbeeldnodes, hun stabiele identiteiten en
verwijzingen. De bestaande technische modelnamen blijven behouden: de roots
staan in de modellen die eindigen op `.Gegevens`, `.Regels` en `.Service`.

De voorbeeldinhoud is in een geïsoleerde native MPS-worker gecontroleerd met de
echte ALEF-modelcontroles en Testspraak-interpreter. Alle vijf tests slagen;
er zijn geen foutdiagnoses. De standaard XML-typemapping geeft alleen informatie.

De oorspronkelijke 15 proefroots zijn vastgelegd in
`out/workspace-sportclub-2026-09-29/before.json`, inclusief de oorspronkelijke
opslagrevisie. Daar staan ook de publicatiegegevens, native XML-export,
testresultaten en schermafbeeldingen. De normale backend bewaart de eerdere
versies in de opslaggeschiedenis. Het voorbeeld wordt via de bestaande
workspace-API opgeslagen, met modelvergrendeling en versiecontrole.

Op 29 september 2026 opgeslagen als revisie
`d419b1749d7c6f937a98563cfa80c12365773243`. Alle opgeslagen nodes en verwijzingen
zijn exact vergeleken met het native geteste voorbeeld; alle vier roots zijn
daarna in de draaiende browsereditor geopend en visueel gecontroleerd.
Het reeds gekoppelde open pakket `v1.6.1 · Test` ging volgens de bestaande
koppelwerking van revisie 11 naar 12. Het definitieve pakket is ongewijzigd.

Later op 29 september 2026 zijn de invoer- en uitvoermapping als afzonderlijke
roots opgeslagen in hetzelfde Service-model, in revisie
`65763e90d6a3caa19e7f0cecc538dddaff2b12f3`. De serviceverwijzingen zijn aangepast;
de mappingvelden, regels en tests zijn inhoudelijk behouden. De opgeslagen
voorbeeldkopie volgt dezelfde indeling. Back-up en publicatiegegevens staan in
`out/separate-service-mappings`.

# RepublicNavyBot

## Open Source
Der Bot ist öffentlich einsehbar, jeder kann den Code sehen und eigene Änderungen einbringen!

Wenn ihr vorhabt euch einzubringen, dann achtet bitte auf einen qualitativ guten Code, der auch performant ist. 
Das sollte kein Spielplatz zum Testen sein, sondern eher für Fortgeschrittene die Möglichkeit geben sich zu beteiligen und das Abändern von veralteten Links etc. einfacher machen.


### GitHub Workflow
Der Master-Branch ist geschützt und kann nicht direkt verändert werden. 

Wenn ihr etwas einbringen wollt, müsst ihr euch einen neuen Branch erstellen, bei diesem gilt es folgende Conventions zu beachten:
- wenn ihr einen Fehler behebt (oder etwas veraltetes austauscht), nennt ihr den Branch `fix-...` und beginnt euren Commit mit `fix: ...`.
- wenn ihr etwas neues einfügt, nennt ihr den Branch `feat-...` und beginnt den Commit mit `feature: ...`

Im Branch beschreibt ihr grob, was der Sinn des Fixes oder Features ist und im Commit beschreibt ihr detailliert, was ihr geändert habt.

Wenn ihr fertig seid, erstellt ihr eine Pull Request, in der ihr von mir ein Review anfordert. Bitte testet eure Änderungen (sofern möglich), bevor ihr eine Pull Request erstellt. Schreibt in jedem Fall, in die Pull Request, ob ihr einen Test durchgeführt habt.

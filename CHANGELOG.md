# 4.3.1

* fix(test): warning fixed
* test(fsm): new test to kill an extreme mutant
* clean(test): test cleaned
* config(pom): adding branch coverage threshold
* config(pom): moving version to snapshot
* config(pom): tools updated

# 4.3.0

* feat(fsm): better typing of FSMs and their transitions
* fix(fsm): use interacto as name for timeout transition thread name
* fix(log): better logging in FSMs
* fix(log): incorrect log message
* clean(test): minor cleaning in test


# 4.2.0

* fix(doc): fix javadoc
* fix(binding): uninstalling a binding must uninstall its interaction
* chore(maven): checkstyle updated
* chore(maven): deps updated
* doc(code): public API documented
* test(fsm): tests added
* clean(test): useless code removed

# 4.1.0

* feat(binding): a new feature for counting the number of times a binding ends/was cancelled
* feat(interaction): base code for concurrent interaction
* feat(interaction): can ask interactions to consume the events they process
* feat(interaction): method flush for interaction data

* fix(binding): cancelling a binding may not cancel an undoable command
* fix(binding): crash when a command is not created but 'first' is called
* fix(binding): in some rare cases, a command with NONE registration policy may not be removed from the command register
* fix(binding): when the creation of a command crashes, the error must be reported
* fix(chore): de-commenting the descartes plugin as the ci issue came from the oracle jdk we used
* fix(chore): fix default maven jobs
* fix(chore): mvn install already includes mvn verify
* fix(chore): run checkstyle and spotbugs during the verify step
* fix(cmd): various improvements, fixes, and cleanings of predefined commands
* fix(doc): fix missing javadoc tags
* fix(fsm): crash when adding fsm handlers with sub-fsm transitions
* fix(fsm): exceptions not collected in timeout transition threads
* fix(instrument): configureBinding may not be called if a binding added using addBinding
* fix(test): flaky tests
* fix(test): test regressions fixed

* governance(license): headers' year updated
* doc(javadoc): documentation added
* doc(readme): readme file created

* chore(ci): add coverage report
* chore(ci): build must fail when coverage is not 100%
* chore(mvn): deps updated
* chore(mvn): must have an extreme mutation score of 100%
* chore(mvn): update descarte config
* chore(build): new checkstyle rules

* test(all): tests added
* test(all): new tests added to kill extreme mutants

* clean(chore): pom file cleaned
* clean(chore): useless file removed
* clean(chore): useless mvn plugin removed from pom
* clean(log): useless file removed (in fact moved to javafx)
* clean(test): useless code removed in tests
* clean(test): various test cleanings

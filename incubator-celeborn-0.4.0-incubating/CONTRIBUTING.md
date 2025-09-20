# Contributing to Celeborn
Any contributions from the open-source community to improve this project are welcome!

## Code Style
This project uses check-style plugins. Run some checks before you create a new pull request.

```shell
dev/reformat
```

If you have changed configuration, run following command to refresh docs.
```shell
UPDATE=1 build/mvn clean test -pl common -am -Dtest=none -DwildcardSuites=org.apache.celeborn.ConfigurationSuite
```

## How to Contribute
For collaboration, feel free to contact us on [Slack](https://join.slack.com/t/apachecelebor-kw08030/shared_invite/zt-1ju3hd5j8-4Z5keMdzpcVMspe4UJzF4Q).
To report a bug, you can just open a ticket on [Jira](https://issues.apache.org/jira/projects/CELEBORN/issues)   
and attach the exceptions and your analysis if any. For other improvements, you can contact us or
open a Jira ticket first and describe what improvement you would like to do. 
After reaching a consensus, you can open a pull request and your pull request 
will get merged after reviewed.

## Improvements on the Schedule
There are already some further improvements on the schedule and welcome to contact us for collaboration:
1. Flink support.
2. Multi-tenant.
3. Support Tez.
4. Rolling upgrade.
5. Multi-layered storage.
6. Enhanced flow control.
7. HA improvement.
8. Enhanced K8S support.
9. Support spilled data.
10. Locality awareness.

## Guidelines
### Adding RPC Messages
When you add new RPC message, it's recommended to follow raw PB message case, for example
`RegisterWorker` and `RegisterWorkerResponse`. The RPC messages will be unified into raw PB messages eventually.

### Using Error Prone
Error Prone is a static analysis tool for Java that catches common programming mistakes at compile-time.

To add the Error Prone plugin in IntelliJ IDEA, start the IDE and find the Plugins dialog. Browse Repositories, choose Category: Build, and find the Error-prone plugin. Right-click and choose 'Download and install'. The IDE will restart after you’ve exited these dialogs.

Allows to build projects using Error Prone Java compiler to catch common Java mistakes at compile-time. To use the compiler, go to 'File | Settings/Preferences | Build, Execution, Deployment | Compiler | Java Compiler' and select 'Javac with error-prone' in 'Use compiler' box.

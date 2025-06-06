# java-hotswap

A mechanism to hotswap Java classes and resources at runtime.

## Overview

This repository aims to build a command-line tool capable of reloading changed classes and resources in a running JVM. The long-term goal is an extensible solution inspired by JRebel that works with standard Maven and Gradle builds.

See [docs/design.md](docs/design.md) for the architecture sketch, proof-of-concept checklist and growth plan.

## Command Line Interface

Run the CLI to manually trigger reloads:

```
java -cp <jar-with-dependencies> org.acmsl.hotswap.cli.ReloadCLI
```

The following commands are available:

* `reload <class-name> <class-file>` – redefine a loaded class with the supplied class file bytes.
* `quit` – exit the CLI.

For example:

```
> reload com.example.MyService target/classes/com/example/MyService.class
> quit
```


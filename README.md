# java-hotswap

A mechanism to hotswap Java classes and resources at runtime.

## Overview

This repository aims to build a command-line tool capable of reloading changed classes and resources in a running JVM. The long-term goal is an extensible solution inspired by JRebel that works with standard Maven and Gradle builds.

See [docs/design.md](docs/design.md) for the architecture sketch, proof-of-concept checklist and growth plan.

## Usage

Attach the agent and provide a YAML configuration file using the `hsconfig` system property:

```
java -javaagent:java-hotswap.jar -Dhsconfig=/path/to/config.yml -cp <classpath> com.example.Main
```

The YAML file may specify a `port` field to change the server socket (defaults to `62345`) and a list of folders to watch (unused for now).


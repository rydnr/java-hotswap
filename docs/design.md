# Java Hotswap Tool Design

This document outlines the architecture, proof-of-concept (POC) experiments, and growth plan for a command-line triggered hot-swap tool. The goal is a minimal yet extensible solution that can reload changed bytecodes and resources during development while remaining compatible with standard Maven/Gradle builds.

## Architecture Sketch

The tool is organized in several modules, each designed for independent evolution:

1. **Agent Bootstrap**
   - A Java agent attached at JVM startup using the `-javaagent` option.
   - Acquires an `Instrumentation` instance for class redefinition.
   - Initializes the reloader core and registers command-line or file-watcher triggers.

2. **Reloader Core**
   - Uses the Instrumentation API and JVMTI to redefine classes and resources.
   - Performs bytecode transformation via ASM/Byte Buddy to enable structural updates. Strategies include renaming original classes and weaving access bridges to preserve object identity.
   - Supports resource replacement through a custom ClassLoader.

3. **Compiler & Trigger**
   - Leverages Maven or Gradle's incremental compiler. The tool monitors the target/classes directory for updated bytecode.
   - A command-line utility (or file watcher) detects changes and invokes the reloader core to apply updates.

4. **IoC Adapters** (future work)
   - Hooks into frameworks like Spring to refresh beans or re-inject dependencies after reloads.
   - Provides extension points for other IoC containers.

### Project Layout

```
/agent       - Java agent entry point and bootstrap code
/reloader    - Core logic for class/resource reloading
/cli         - Command-line interface and file watcher
/ioc-adapter - Optional integrations with IoC frameworks
```

Each module will be a Maven module so that users can include the agent and CLI as dependencies in existing builds.

## Proof-of-Concept Checklist

1. **HotSwap Invocation**
   - Compile a simple POJO project with Maven.
   - Attach the agent at JVM start and issue a command to reload an updated class file.

2. **Hierarchy Tweaks**
   - Validate class renaming + bridge weaving to support changes in superclasses or interfaces.
   - Ensure object identity and existing references remain intact after reload.

3. **Resource Updates**
   - Replace a resource (e.g., property file) in the classpath and confirm new values are served from the custom ClassLoader.

4. **Incremental Compiler**
   - Run Maven's incremental compiler from the tool and pick up compiled class files from `target/classes`.

5. **Command-Line Trigger**
   - Implement a simple CLI that listens for user commands (`reload`, `quit`) or auto-detects file modifications via WatchService.

These experiments verify that runtime redefinition works and that the pipeline from file change to reload is viable.

## Growth Plan

1. **Logging & Diagnostics**
   - Integrate a logging framework (SLF4J + Logback) for structured logs.
   - Provide verbose and debug modes for troubleshooting reload issues.

2. **IDE Plugins**
   - Offer plugins for IntelliJ and Eclipse to invoke the tool directly from the IDE.
   - Expose an API to query reload status.

3. **Robust IoC Support**
   - Develop Spring adapters that refresh bean scopes and re-proxy beans as needed.
   - Allow user-defined strategies for custom containers.

4. **Resource Change Strategies**
   - Handle configuration reloads (e.g., YAML or properties files) with minimal downtime.
   - Add hooks for frameworks to react to resource updates.

5. **Testing & CI**
   - Provide integration tests that launch sample applications and perform reloads.
   - Set up a CI pipeline (GitHub Actions) for build and release automation.

6. **Licensing & Packaging**
   - Use the [GNU General Public License version 3](https://www.gnu.org/licenses/gpl-3.0.txt) for the open-source components while reserving rights for premium features.
   - Publish artifacts to Maven Central for easy consumption.

7. **Polished Product**
   - Offer a graphical dashboard showing reload history and status.
   - Document usage patterns, limitations, and troubleshooting steps.

The aim is to evolve from a functional prototype into a well-documented, user-friendly tool with optional commercial extensions.


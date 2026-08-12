# Issue: Jetty hot-reload kills Spring's DispatcherServlet

**Status:** open, workaround in use. Found 2026-08-13 while verifying `EmployeeController`'s `/api/{id}` endpoints.

## Symptom

After `mvn jetty:run` has been running for a while and its file-watcher (`<scan>10</scan>` in `pom.xml`) triggers an automatic redeploy, **every request that should be routed by Spring's `DispatcherServlet` starts 404ing**, including endpoints that worked seconds earlier (`EmployeeController`'s `/api/{id}` routes, and previously-verified ones like `/employee/v3/api-docs`). The 404 page's `SERVLET:` field reads `default` (Jetty's static-file servlet) instead of `dispatcher`, meaning the request never reaches Spring at all — it's not a routing/mapping bug in application code, DispatcherServlet is simply gone.

The raw-servlet layer (`ApiServlet` at exact path `/api`, `HelloWorldServlet` at `/hello`) is unaffected — only the Spring MVC layer breaks.

## Evidence

Captured via `mvn jetty:run` (without `-q`, so plugin/container logs aren't suppressed):

```
[INFO] 2 Spring WebApplicationInitializers detected on classpath
...
[INFO] Initializing Spring DispatcherServlet 'dispatcher'
...
[INFO] Started oejs.ServerConnector@...{0.0.0.0:9000}
[INFO] Scan interval sec = 10
[INFO] Restarting oeje10mp.MavenWebAppContext@...
...
[INFO] 1 Spring WebApplicationInitializers detected on classpath
[INFO] Started oeje10mp.MavenWebAppContext@...
[INFO] Restart completed at ...
```

On the **first** boot, `DispatcherServlet` initializes normally (logged, and confirmed working via curl). Moments later the scanner fires an automatic **restart** — and on that second pass, `"Initializing Spring DispatcherServlet 'dispatcher'"` never appears again, and the WebApplicationInitializer count drops from 2 to 1. No exception is logged for this, anywhere — likely because "SLF4J: No SLF4J providers were found" (also in the log) means the JVM is running with a no-op SLF4J binding, so anything Spring/Jetty logs internally via SLF4J (including a startup failure) is silently swallowed.

## Likely cause (not fully confirmed)

Two candidate contributors, not mutually exclusive:

1. **Silent failure during hot-redeploy**, hidden by the missing SLF4J provider — the actual exception (if any) is never printed anywhere, so root-causing further would require adding a real SLF4J binding (e.g. `slf4j-simple`) temporarily to unmask it.
2. **A second `WebApplicationInitializer` on the classpath**: `spring-boot-autoconfigure-3.3.0.jar` (pulled in transitively, likely via `springdoc-openapi-starter-webmvc-ui`) contains `JerseyAutoConfiguration$JerseyWebApplicationInitializer`, a *concrete* class implementing `WebApplicationInitializer`. This project isn't Spring Boot and doesn't use Jersey, but `SpringServletContainerInitializer` (plain servlet-spec bootstrapping, not Boot-aware) doesn't know that — it instantiates and calls `onStartup()` on every concrete `WebApplicationInitializer` it finds, unconditionally. That matches the "2 detected" count on first boot (our own `WebInitializer` + this one). Whether it's implicated in the restart failure itself, or just an unrelated red flag worth cleaning up, isn't confirmed.

## Workaround used this session

Don't test against a server that's already been auto-restarted — kill any running instance, `mvn clean` (to also rule out stale `target/classes` from before this bug was noticed), then `mvn jetty:run` and test **immediately** after `Started oejs.ServerConnector` appears in the log, before the scan interval elapses and triggers a redeploy. All of `EmployeeController`'s endpoints were verified working this way (GET/PUT/DELETE `/api/{id}`, 404 on missing id).

This is not a real fix — it just avoids exercising the broken path during verification. The edit/test loop `flow.md` describes ("no manual restart needed") does not actually hold today.

## How to actually resolve it (not done yet — flagging for a follow-up session)

1. Add a real SLF4J binding temporarily (e.g. `slf4j-simple`, test/provided scope) to unmask what's actually happening during the redeploy, then remove it once diagnosed.
2. Exclude `spring-boot-autoconfigure` from `springdoc-openapi-starter-webmvc-ui` (`<exclusions>` in `pom.xml`) since this project isn't Spring Boot and doesn't need it — removes the stray `JerseyWebApplicationInitializer` and narrows the search space regardless of whether it's the actual cause.
3. Re-test the hot-reload cycle specifically (edit a file while `jetty:run` is live, wait past the scan interval, confirm `/api/{id}` and `/v3/api-docs` still respond) to confirm whichever fix actually closes the gap.

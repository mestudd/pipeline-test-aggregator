Pipeline Test Aggregator Plugin
===============================

About
-----

Allows you to aggregate tests from run builds into a pipeline.

Usage
-----

Aggregate the test results, if any, from a build.

```groovy
def job = build job: 'downstream'
aggregateTests job
```

Note: I do not know what happens if you call both ```junit``` and
```aggregateTests``` in the same pipeline.

----

Changelog
=========

## Version 0.1
* Initial release
* Support JUnit test results
* Support Pipeline Test Aggregator test results

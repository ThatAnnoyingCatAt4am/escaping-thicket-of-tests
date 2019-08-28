Escaping the thicket of tests
=========

The code here exists to illustrate and provide an example for the ideas of this article: [in Russian](https://habr.com/post/463623/), [in English](https://habr.com/post/465211/). This is not an actively maintained repository. 

---

##### Contents:

* [The test lifecycle functions, fixture generation and other stuff](../blob/master/src/main/scala)
* [A test suite example](../blob/master/src/test/scala/MyTest.scala)
* [Database schema of the test app](../blob/master/src/test/resources/db/migration/V0001.0__Init.sql)

---

To try the tests out for yourself, [get the latest sbt](https://www.scala-sbt.org/download.html) and execute
```
sbt test
```

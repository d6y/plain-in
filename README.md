# An IN clause for Plain SQL

`PlainSQLExtras` defines `++` and `Fragment.inValues`.

Use it like this:

```
import PlainSQLExtras._

  val values: Seq[Long] = List(5, 1, 4)

val q = (
  sql"select id from message where id in " ++ Fragment.inValues(values) ++ sql" order by id"
).as[(Long, Option[String])]

```

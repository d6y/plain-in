# An IN clause for Plain SQL

`PlainSQLExtras` defines `++` and `inValues`.

Use it like this:

```
import PlainSQLExtras._

val values: Seq[Long] = List(5, 1, 4)

 val query = (
   sql"select id from table where id in ".inValues(values) ++ sql" order by id"
 ).as[Long]
```

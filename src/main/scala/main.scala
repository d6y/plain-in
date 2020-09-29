import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object PlainSQLExtras {
  // Via: https://github.com/slick/slick/issues/1161#issuecomment-165996857
  import slick.jdbc.{PositionedParameters, SQLActionBuilder, SetParameter}

  object Fragment {
    def inValues[T: SetParameter](xs: Seq[T]): SQLActionBuilder =
      xs match {
        case Nil => sql"()"
        case head :: tail =>
          val tailValues = tail.map(x => sql",$x")
          sql"(" ++ tailValues.fold(sql"$head")(concat) ++ sql")"
      }
  }

  private def concat(a: SQLActionBuilder, b: SQLActionBuilder): SQLActionBuilder =
    SQLActionBuilder(a.queryParts ++ b.queryParts, new SetParameter[Unit] {
      def apply(p: Unit, pp: PositionedParameters): Unit = {
        a.unitPConv.apply(p, pp)
        b.unitPConv.apply(p, pp)
      }
    })

  implicit class PlainOps(left: SQLActionBuilder) {
    def ++(right: SQLActionBuilder): SQLActionBuilder = concat(left, right)
  }
}

object Example {

  final case class Message(sender: String, content: Option[String], id: Long = 0L)

  def freshTestData = Seq(
    Message("Dave", Some("Hello, HAL. Do you read me, HAL?")),
    Message("HAL", Some("Affirmative, Dave. I read you.")),
    Message("Dave", Some("Open the pod bay doors, HAL.")),
    Message("HAL", Some("I'm sorry, Dave. I'm afraid I can't do that.")),
    Message("Dave", None)
  )

  final class MessageTable(tag: Tag) extends Table[Message](tag, "message") {

    def id      = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def sender  = column[String]("sender")
    def content = column[Option[String]]("content")

    def * = (sender, content, id).mapTo[Message]
  }

  lazy val messages = TableQuery[MessageTable]

  import PlainSQLExtras._

  val values: Seq[Long] = List(5, 1, 4)

  val q = (
    sql"""select "id", "content" from "message" where "id" in """ ++ Fragment.inValues(values) ++ sql""" order by "id" desc """
  ).as[(Long, Option[String])]

  val program = for {
    _       <- messages.schema.create
    _       <- messages ++= freshTestData
    results <- q
  } yield results

  def main(args: Array[String]): Unit = {
    val db = Database.forConfig("example")
    try Await.result(db.run(program), 2.seconds).foreach(println)
    finally db.close
  }
}

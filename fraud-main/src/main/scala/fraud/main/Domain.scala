package fraud.main

import org.apache.spark.mllib.linalg.Vectors
import spray.json.DefaultJsonProtocol

case class Transaction(id: String, user: String, receiver: String, amount: String, timestamp: String)

object TransactionJsonProtocol extends DefaultJsonProtocol {
  implicit val TransactionFormat = jsonFormat5(Transaction)
}

object Domain {
  val receivers = Seq("Albert Hein", "E-Bay", "Power Company")
  val receiverIds = Map(receivers(0) -> 0, receivers(1) -> 1, receivers(2) -> 2)

  def features(t: Transaction) = Vectors.dense(receiverId(t), amountId(t))

  def receiverId(t: Transaction): Int = receiverIds(t.receiver)

  def amountId(t: Transaction): Int = {
    val amount = t.amount.toDouble
    if (amount < 0.0) throw new IllegalArgumentException(s"Amount can not be negative, amount = $amount")
    if (amount < 1.00) 0
    else if (amount < 100) 1
    else 2
  }

}

object RandomTransaction {

  val rnd = new scala.util.Random()
  def randomTransaction() = apply()

  def apply(): Transaction = Transaction(randomGiud, randomGiud, randomReceiver, randomAmount, timestamp)

  def randomGiud() = java.util.UUID.randomUUID.toString
  def randomReceiver() = Domain.receiverIds.keys.toSeq(rnd.nextInt(Domain.receiverIds.keys.size))
  def randomSmallAmount() = (rnd.nextInt(99).toDouble / 100)
  def randomMediumAmount() = rnd.nextInt(99) + (rnd.nextInt(99).toDouble / 100)
  def randomLargeAmount() = rnd.nextInt(999) + (rnd.nextInt(99).toDouble / 100)
  def randomAmount() = Seq(randomSmallAmount, randomMediumAmount, randomLargeAmount)(rnd.nextInt(3)).toString()
  def timestamp() = new java.util.Date().toString()

  private def generateRandom(minBound: Int, maxBound: Int): Unit ={

  }

}

package fraud.main

import org.apache.spark.mllib.linalg.Vectors


case class Transaction(id: String, user: String, receiver: String, amount: String, timestamp: String)


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

}

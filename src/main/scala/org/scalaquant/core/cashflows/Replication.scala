package org.scalaquant.core.cashflows

object Replication {
  sealed trait Type
  case object Sub extends Type
  case object Central extends Type
  case object Super extends Type
}

case class DigitalReplication(replicationType: Replication.Type = Replication.Central, gap: Double = 1e-4)
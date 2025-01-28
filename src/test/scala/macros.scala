import scala.quoted._

inline def verifyEffectUsage[F[_], A](inline expr: => F[A]): F[A] = ${ 
  verifyEffectMacro('expr) 
}

def verifyEffectMacro[F[_], A](expr: Expr[F[A]])(using Quotes, Type[F[A]]): Expr[F[A]] = {
  import quotes.reflect._

  def checkEffectProperties(term: Term): Boolean = {
    term.show.contains("IO") && 
    !term.show.contains("unsafeRun")
  }

  expr.asTerm match {
    case term if checkEffectProperties(term) => expr
    case _ => 
      report.error("Improper effect usage detected")
      expr
  }
}

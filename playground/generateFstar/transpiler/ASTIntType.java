/* Generated By:JJTree: Do not edit this line. ASTIntType.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTIntType extends SimpleNode {
  public ASTIntType(int id) {
    super(id);
  }

  public ASTIntType(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fc65d67b485e52feaffd303f0bc6df5d (do not edit this line) */
/* Generated By:JJTree: Do not edit this line. ASTPrimitiveType.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTPrimitiveType extends SimpleNode {
  public ASTPrimitiveType(int id) {
    super(id);
  }

  public ASTPrimitiveType(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1c000df87d989e5f2d5618d1def4e154 (do not edit this line) */

/* Generated By:JJTree: Do not edit this line. ASTUnaryExpression.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTUnaryExpression extends SimpleNode {
  public ASTUnaryExpression(int id) {
    super(id);
  }

  public ASTUnaryExpression(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4b774c41d1e11e362d9a4047d4381c60 (do not edit this line) */

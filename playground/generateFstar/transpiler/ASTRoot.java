/* Generated By:JJTree: Do not edit this line. ASTRoot.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTRoot extends SimpleNode {
  public ASTRoot(int id) {
    super(id);
  }

  public ASTRoot(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=a28ade855b293bb05cdecfb936123b1e (do not edit this line) */
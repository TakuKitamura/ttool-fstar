/* Generated By:JJTree: Do not edit this line. ASTCharType.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTCharType extends SimpleNode {
  public ASTCharType(int id) {
    super(id);
  }

  public ASTCharType(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=62ec2251a6971de7da1c05bd545db658 (do not edit this line) */
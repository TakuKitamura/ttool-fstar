/* Generated By:JJTree: Do not edit this line. ASTFormalParameters.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTFormalParameters extends SimpleNode {
  public ASTFormalParameters(int id) {
    super(id);
  }

  public ASTFormalParameters(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=57cd13e543b5dc715df6e04e53fe726d (do not edit this line) */
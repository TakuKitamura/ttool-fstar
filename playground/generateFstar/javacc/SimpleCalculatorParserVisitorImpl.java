/* Generated By:JavaCC: Do not edit this line. SimpleCalculatorParserVisitor.java Version 4.1d1 */

import java.util.ArrayList;
import java.util.List;

// TODO: マイナス符号の対応を検討
// TODO: 整数以外の型への対応

public class SimpleCalculatorParserVisitorImpl implements SimpleCalculatorParserVisitor {

    public boolean haveMinusSign = false;

    public String rep(List<String> children, List<String> ops) {
        String x = children.get(0);
        String y = children.get(1);
        System.out.println(ops);

        String op = ops.get(0);
        String ret = String.format("(%s %s %s)", op, x, y);
        if (op.equals("not I32.eq")) {
            String[] sepalate_op = op.split(" ", 2);
            String not = sepalate_op[0]; // not
            op = sepalate_op[1]; // I32
            ret = String.format("(%s (%s %s %s))", not, op, x, y);
        }

        if (children.size() > 2) {
            List<String> nextChildren = new ArrayList<String>();
            List<String> nextOps = new ArrayList<String>();
            nextChildren.add(ret);
            for (int i = 2; i < children.size(); i++) {
                nextChildren.add(children.get(i));
            }
            for (int i = 1; i < ops.size(); i++) {
                nextOps.add(ops.get(i));
            }
            return rep(nextChildren, nextOps);
        }

        return ret;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        System.out.println(node);
        return null;
    }

    @Override
    public Object visit(ASTRoot node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        System.out.println(node);
        String ret = "(";

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            ret += leaf;
            if (i != leafNum - 1) {
                ret += " || ";
            }
        }
        ret += ")";

        System.out.println(ret);
        return ret;
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        System.out.println(node);
        String ret = "(";

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            ret += leaf;
            if (i != leafNum - 1) {
                ret += " && ";
            }
        }

        ret += ")";

        System.out.println(ret);
        return ret;
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        System.out.println(node);

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        List<String> ops = (List<String>) node.jjtGetValue();
        List<String> fstarOps = new ArrayList<>();

        for (int i = 0; i < ops.size(); i++) {
            if (ops.get(i).equals("==")) {
                fstarOps.add("I32.eq");
            } else if (ops.get(i).equals("!=")) {
                fstarOps.add("not I32.eq");
            } else {
                System.out.println("unkown EqualityExpression ope");
            }
        }

        List<String> leafs = new ArrayList<>();
        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            leafs.add(leaf);
        }

        String ret = rep(leafs, fstarOps);

        System.out.println(ret);

        return ret;
    }

    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        System.out.println(node);

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        List<String> ops = (List<String>) node.jjtGetValue();
        List<String> fstarOps = new ArrayList<>();

        for (int i = 0; i < ops.size(); i++) {
            if (ops.get(i).equals("<")) {
                fstarOps.add("I32.lt");
            } else if (ops.get(i).equals("<=")) {
                fstarOps.add("I32.lte");
            } else if (ops.get(i).equals(">")) {
                fstarOps.add("I32.gt");
            } else if (ops.get(i).equals(">=")) {
                fstarOps.add("I32.gte");
            } else {
                System.out.println("unkown EqualityExpression ope");
            }
        }

        List<String> leafs = new ArrayList<>();
        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            leafs.add(leaf);
        }

        String ret = rep(leafs, fstarOps);

        System.out.println(ret);

        return ret;
    }

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        System.out.println(node);
        if (node.jjtGetValue() != null && node.jjtGetValue().toString().equals("-")) {
            this.haveMinusSign = true;
        }
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        System.out.println(node);
        System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTInteger node, Object data) {
        System.out.println(node);

        String ret = (String) node.jjtGetValue();

        if (this.haveMinusSign) {
            ret = "-" + ret;
            this.haveMinusSign = false;
        }

        System.out.println(ret);

        return ret;
    }

    @Override
    public Object visit(ASTFloating node, Object data) {
        System.out.println(node);

        String ret = (String) node.jjtGetValue();

        if (this.haveMinusSign) {
            ret = "-" + ret;
            this.haveMinusSign = false;
        }

        System.out.println(ret);

        return ret;
    }

    @Override
    public Object visit(ASTCharacter node, Object data) {
        System.out.println(node);
        System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTString node, Object data) {
        System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }
}
/*
 * JavaCC - OriginalChecksum=b885522fef29c058f490c57bdc41604f (do not edit this
 * line)
 */

package fstar.transpiler;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// TODO: 整数以外の型への対応
// TOOD: Exceptionクラスを具体的なものに変更

public class FstarTranspilerVisitorImpl implements FstarTranspilerVisitor {

    public FstarTranspilerVisitorImpl() {

    }

    public FstarTranspilerVisitorImpl(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    // 関数宣言の読み取り結果
    private MethodDeclaration methodDeclaration = null;

    public Object filterObjException(Object obj) throws Exception {
        if (obj instanceof Exception) {
            Exception e = (Exception) obj;
            throw e;
        }
        return obj;
    }

    private String generateFstarFormat(List<String> rawValues, String op) throws Exception {

        System.out.println(rawValues);
        System.out.println(op);

        Map<String, String> fstarTypeMap = new HashMap<String, String>() {
            {
                put("int32", "I32");
            }
        };

        Map<String, String> typeSuffixMap = new HashMap<String, String>() {
            {
                put("int32", "l");
            }
        };

        String xRawValue = rawValues.get(0);

        String xFstarType = null;

        String xVariableType = methodDeclaration.args.get(xRawValue);

        if (methodDeclaration.args.get(xRawValue) != null) { // variable
            xFstarType = fstarTypeMap.get(xVariableType);
        } else if (xRawValue.equals("ret") == true) { // ret
            xVariableType = methodDeclaration.returnType;
            xFstarType = fstarTypeMap.get(methodDeclaration.returnType);
        } else { // value

        }

        String yRawValue = rawValues.get(1);

        String yFstarType = null;

        String yVariableType = methodDeclaration.args.get(yRawValue);

        if (methodDeclaration.args.get(yRawValue) != null) { // variable
            yFstarType = fstarTypeMap.get(yVariableType);
        } else if (yRawValue.equals("ret") == true) { // ret
            yVariableType = methodDeclaration.returnType;
            yFstarType = fstarTypeMap.get(methodDeclaration.returnType);
        } else { // value

        }

        System.out.printf("xVariableType = %s, yVariableType = %s\n", xVariableType, yVariableType);
        if (xVariableType == null && yVariableType != null) {
            String suffix = typeSuffixMap.get(yVariableType);
            xRawValue += suffix;
        } else if (xVariableType != null && yVariableType == null) {
            String suffix = typeSuffixMap.get(xVariableType);
            yRawValue += suffix;
        }

        System.out.printf("%s, %s\n", xRawValue, yRawValue);

        if (xFstarType == null && yFstarType == null && xRawValue.equals("ret") == false
                && yRawValue.equals("ret") == false) {
            throw new Exception("find no need formula");
        }

        String type = null;

        if (xFstarType != null) {
            System.out.printf("xFstarType: %s\n", xVariableType);
            type = xFstarType;
        } else {
            System.out.printf("yFstarType: %s\n", yVariableType);
            type = yFstarType;
        }

        Map<String, String> opeMap = new HashMap<String, String>() {
            {
                put("==", "eq");
                put("!=", "neq"); // fstar don't have neq ope
                put("<", "lt");
                put("<=", "lte");
                put(">", "gt");
                put(">=", "gte");
            }
        };

        String fstarOpName = opeMap.get(op); // I32
        String fstarOp = String.format("%s.%s", type, fstarOpName); // gt

        if (xRawValue.startsWith("-")) {
            xRawValue = String.format("(%s)", xRawValue);
        }

        if (yRawValue.startsWith("-")) {
            yRawValue = String.format("(%s)", yRawValue);
        }

        String ret = String.format("(%s %s %s)", fstarOp, xRawValue, yRawValue);
        if (op.equals("neq")) {
            String[] sepalate_op = op.split(" ", 2);
            String not = sepalate_op[0]; // not
            op = sepalate_op[1]; // I32
            ret = String.format("(%s (%s %s %s))", not, fstarOp, xRawValue, yRawValue);
        }

        return ret;
    }

    private Object convertToFstarSpecFormat(Node node) throws Exception {

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null)).toString();
        }

        String op = null;

        if (node instanceof ASTEqualityExpression) {
            ASTEqualityExpression equalityNode = (ASTEqualityExpression) node;
            op = (String) equalityNode.jjtGetValue();
        } else if (node instanceof ASTRelationalExpression) {
            ASTRelationalExpression relationalNode = (ASTRelationalExpression) node;
            op = (String) relationalNode.jjtGetValue();
        } else {
            throw new Exception("find invalid node");
        }

        List<String> leafs = new ArrayList<>();
        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = filterObjException(n.jjtAccept(this, null)).toString();
            leafs.add(leaf);
        }

        String ret = generateFstarFormat(leafs, op);

        return ret;
    }

    private Object assembleAndOrExpression(Node node, String ope) throws Exception {
        String ret = "(";

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null)).toString();
        }

        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = filterObjException(n.jjtAccept(this, null)).toString();
            ret += leaf;
            if (i != leafNum - 1) {
                ret += String.format(" %s ", ope);
            }
        }
        ret += ")";

        return ret;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        System.out.println(node);
        return null;
    }

    @Override
    public Object visit(ASTConditionRoot node, Object data) {
        System.out.println(node);
        try {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        System.out.println(node);
        try {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        System.out.println(node);

        try {
            return assembleAndOrExpression(node, "||");
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        System.out.println(node);
        try {
            return assembleAndOrExpression(node, "&&");
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        System.out.println(node);
        try {
            return convertToFstarSpecFormat(node);
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        System.out.println(node);
        try {
            return convertToFstarSpecFormat(node);
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        System.out.println(node);
        try {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        System.out.println(node);
        try {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        System.out.println(node);
        try {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTVariable node, Object data) {
        System.out.println(node);
        try {

            String argName = (String) node.jjtGetValue();

            if (methodDeclaration.args.get(argName) == null && argName.equals(argName) == false) {
                throw new Exception("find unkown variable");
            }

            return argName;
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTInteger node, Object data) {
        System.out.println(node);
        try {
            String ret = (String) node.jjtGetValue();
            return ret;
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTFloating node, Object data) {
        System.out.println(node);
        try {
            String ret = (String) node.jjtGetValue();
            return ret;
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTCharacter node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTString node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }
    }

    // Method

    @Override
    public Object visit(ASTMethodDeclarationRoot node, Object data) {
        System.out.println(node);
        try {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        System.out.println(node);

        try {
            Node resultTypeNode = (Node) node.jjtGetChild(0);
            while (true) {
                if (resultTypeNode.jjtGetNumChildren() == 0) {
                    methodDeclaration.returnType = (String) filterObjException(resultTypeNode.jjtAccept(this, null));
                    break;
                }
                resultTypeNode = resultTypeNode.jjtGetChild(0);
            }

            return filterObjException(node.jjtGetChild(1).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTResultType node, Object data) {
        System.out.println(node);
        try {
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        System.out.println(node);

        try {
            String functionName = (String) node.jjtGetValue();
            methodDeclaration.funcName = functionName;
            return filterObjException(node.jjtGetChild(0).jjtAccept(this, null));
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        System.out.println(node);

        try {
            methodDeclaration.args = new HashMap<String, String>();

            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                Node formalParameters = (Node) node.jjtGetChild(i);
                Node formalParameter = (Node) formalParameters.jjtGetChild(0);
                Node variableDeclaratorId = (Node) formalParameters.jjtGetChild(1);

                String argType = "";
                String argName = "";
                while (true) {
                    if (formalParameter.jjtGetNumChildren() == 0) {
                        argType = (String) filterObjException(formalParameter.jjtAccept(this, null));
                        break;
                    }

                    formalParameter = formalParameter.jjtGetChild(0);

                }

                argName += (String) filterObjException(variableDeclaratorId.jjtAccept(this, null));
                if (variableDeclaratorId.jjtGetNumChildren() == 1) {
                    argName += "[]";
                }

                methodDeclaration.args.put(argName, argType);
            }

            return null;
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        System.out.println(node);
        return null;
    }

    public Object visit(ASTVoidType node, Object data) {
        System.out.println(node);

        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }

    }

    public Object visit(ASTType node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetChild(0).jjtAccept(this, null);
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTArrayBrackets node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTPrimitiveType node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTPrimitiveBlacketType node, Object data) {
        System.out.println(node);
        try {
            return node.jjtGetValue();
        } catch (Exception e) {
            return e;
        }
    }
}

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
    public MethodDeclaration methodDeclaration = null;

    public final Map<String, String> fstarTypeMap = new HashMap<String, String>() {
        {
            put("int8", "I8");
            put("int16", "I16");
            put("int32", "I32");
            put("int64", "I64");
            put("uint8", "U8");
            put("uint16", "U16");
            put("uint32", "U32");
            put("uint64", "U64");

            put("int8[]", "B.buffer I8");
            put("int16[]", "B.buffer I16");
            put("int32[]", "B.buffer I32");
            put("int64[]", "B.buffer I64");

            put("uint8[]", "B.buffer U8");
            put("uint16[]", "B.buffer U16");
            put("uint32[]", "B.buffer U32");
            put("uint64[]", "B.buffer U64");
        }
    };

    public final Map<String, String> typeSuffixMap = new HashMap<String, String>() {
        {
            put("int8", "y");
            put("int16", "s");
            put("int32", "l");
            put("int64", "L");
            put("uint8", "uy");
            put("uint16", "us");
            put("uint32", "ul");
            put("uint64", "UL");
        }
    };

    final Map<String, String> opeMap = new HashMap<String, String>() {
        {
            put("==", "eq");
            put("!=", "neq"); // fstar don't have
                              // neq ope
            put("<", "lt");
            put("<=", "lte");
            put(">", "gt");
            put(">=", "gte");
        }
    };

    public Object filterObjException(Object obj) throws Exception {
        if (obj instanceof Exception) {
            Exception e = (Exception) obj;
            throw e;
        }
        return obj;
    }

    private String tmpSearchingLiteral = null;

    public List<String> usedArgsInRequire = new ArrayList<String>();
    public List<String> usedArgsInEnsure = new ArrayList<String>();

    private int callConditionRootCounter = 0; // 1のときrequireParse, 2のときensureParse

    private String generateFstarFormat(List<String> rawValues, List<String> types, String op) throws Exception {

        System.out.println(rawValues);
        System.out.println(op);

        String xRawValue = rawValues.get(0);
        String yRawValue = rawValues.get(1);

        // int32 x, x > 0 となったとき、'0'はintegerのどの型であるかを推論
        String xVariableType = types.get(0);
        String yVariableType = types.get(1);

        // System.out.println(xVariableType);
        // System.out.println(yVariableType);

        boolean xIsNumber = false;
        boolean yIsNumber = false;

        if (xVariableType.equals("unkowonIntType") && fstarTypeMap.get(yVariableType) != null) {
            xVariableType = yVariableType;
            xIsNumber = true;
        } else if (yVariableType.equals("unkowonIntType") && fstarTypeMap.get(xVariableType) != null) {
            yVariableType = xVariableType;
            yIsNumber = true;
        }

        System.out.println(123);
        // x, yの型が異なる場合
        if (xVariableType.equals(yVariableType) == false) { // string == int32, bool == string
            throw new Exception("each type is different.");
        } else { // string == string, int32 == int32

        }

        String xFstarType = null;

        // String xVariableType = methodDeclaration.args.get(xRawValue);

        if (methodDeclaration.args.get(xRawValue) != null) { // variable
            xFstarType = fstarTypeMap.get(xVariableType);
        } else if (xRawValue.equals("ret") == true) { // ret
            xFstarType = fstarTypeMap.get(methodDeclaration.returnType);
        } else { // value

        }

        String yFstarType = null;

        // String yVariableType = methodDeclaration.args.get(yRawValue);

        if (methodDeclaration.args.get(yRawValue) != null) { // variable
            yFstarType = fstarTypeMap.get(yVariableType);
        } else if (yRawValue.equals("ret") == true) { // ret
            yFstarType = fstarTypeMap.get(methodDeclaration.returnType);
        } else { // value

        }

        System.out.printf("xVariableType = %s, yVariableType = %s\n", xVariableType, yVariableType);

        // 数字であればsuffixを追加
        if (xIsNumber) {
            String suffix = typeSuffixMap.get(yVariableType);
            xRawValue += suffix;
        } else if (yIsNumber) {
            String suffix = typeSuffixMap.get(xVariableType);
            yRawValue += suffix;
        }

        System.out.printf("%s, %s\n", xRawValue, yRawValue);

        // 2 > 1 のような無駄な条件は無効
        if (xFstarType == null && yFstarType == null && xRawValue.equals("ret") == false
                && yRawValue.equals("ret") == false) {
            throw new Exception("find no need formula");
        }

        String type = null; // lt, gt など
        if (xFstarType != null) {
            System.out.printf("xFstarType: %s\n", xVariableType);
            type = xFstarType;
        } else {
            System.out.printf("yFstarType: %s\n", yVariableType);
            type = yFstarType;
        }

        String fstarOpName = opeMap.get(op); // I32
        String fstarOp = String.format("%s.%s", type, fstarOpName); // gt

        // -3l などの数字の場合はカッコで囲う
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
            System.out.println(333);
            System.out.println(this.tmpSearchingLiteral);
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
        List<String> leafsType = new ArrayList<>();
        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            System.out.printf("node = %s\n", n);
            String leaf = filterObjException(n.jjtAccept(this, null)).toString();

            if (this.tmpSearchingLiteral == null) {
                if (leaf.equals("ret")) {
                    System.out.println(1);
                    leafsType.add(methodDeclaration.returnType);
                } else {
                    System.out.println(2);
                    leafsType.add(methodDeclaration.args.get(leaf));
                }
            } else {
                System.out.println(3);
                leafsType.add(this.tmpSearchingLiteral);
            }

            leafs.add(leaf);
            this.tmpSearchingLiteral = null;
        }

        System.out.printf("leafType === %s\n", leafsType);

        String ret = generateFstarFormat(leafs, leafsType, op);

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

        this.tmpSearchingLiteral = null; // requireを探査したあとに、初期状態に戻したいため
        callConditionRootCounter += 1;
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
    public Object visit(ASTValueOrExpr node, Object data) {
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

            if (callConditionRootCounter == 1) {
                usedArgsInRequire.add(argName);
            } else if (callConditionRootCounter == 2) {
                usedArgsInEnsure.add(argName);
            } else {
                throw new Exception("unexpected call astConditionRoot");
            }

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

            // 正確な型はこの時点では不明
            this.tmpSearchingLiteral = "unkowonIntType";
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
            this.tmpSearchingLiteral = "unkowonFloatType";
            return ret;
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTCharacter node, Object data) {
        System.out.println(node);
        try {
            String ret = (String) node.jjtGetValue();
            this.tmpSearchingLiteral = "char";
            return ret;
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTString node, Object data) {
        System.out.println(node);
        try {
            String ret = (String) node.jjtGetValue();
            this.tmpSearchingLiteral = "string";
            return ret;
        } catch (Exception e) {
            return e;
        }

    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        System.out.println(node);
        try {
            String ret = (String) node.jjtGetValue();
            this.tmpSearchingLiteral = "bool";
            return ret;
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        System.out.println(node);
        try {
            String ret = (String) node.jjtGetValue();
            this.tmpSearchingLiteral = "null";
            return ret;
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

                // print

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

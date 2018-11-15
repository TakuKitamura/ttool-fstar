/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */




package proverifspec;

/**
 * Class ProVerifPitypeSyntaxer
 * Creation: 13/09/2015
 * @version 1.0 13/09/2015
 * @author Florian LUGOU
 */
public class ProVerifPitypeSyntaxer extends ProVerifSyntaxer {

    protected void translateConst (ProVerifConst _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "const " + _node.name + ": " + _node.type + " [data].";
    }

    protected void translateFunc (ProVerifFunc _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "fun " + _node.name + " (";
        boolean first = true;
        for (String type: _node.types) {
            if (first)
                first = false;
            else
                this.fullSpec += ", ";
            this.fullSpec += type;
        }
        this.fullSpec += "): " + _node.returnType;
        if (_node.reduc != null)
            this.translateReducAux (_node.reduc, _alinea+1);
        if (_node.priv)
            this.fullSpec += " [private]";
        this.fullSpec += ".";
    }

    private void translateReducAux (ProVerifReduc _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "reduc ";
        if (_node.vars.length > 0) {
            this.fullSpec += "forall ";
            boolean first = true;
            for (ProVerifVar var: _node.vars) {
                if (first)
                    first = false;
                else
                    this.fullSpec += ", ";
                this.fullSpec += var.name + ": " + var.type;
            }
            this.fullSpec += "; ";
        }
        this.fullSpec += _node.formula;

        ProVerifReduc otherwise = _node.otherwise;
        while (otherwise != null) {
            this.fullSpec += "\n" + printAlinea (_alinea);
            this.fullSpec += "      otherwise ";
            if (otherwise.vars.length > 0) {
                this.fullSpec += "forall ";
                boolean first = true;
                for (ProVerifVar var: otherwise.vars) {
                    if (first)
                        first = false;
                    else
                        this.fullSpec += ", ";
                    this.fullSpec += var.name + ": " + var.type;
                }
                this.fullSpec += "; ";
            }
            this.fullSpec += otherwise.formula;
            otherwise = otherwise.otherwise;
        }
    }

    protected void translateReduc (ProVerifReduc _node, int _alinea) {
        this.translateReducAux (_node, _alinea);

        if (_node.priv)
            this.fullSpec += " [private]";
        this.fullSpec += ".";
    }

    protected void translateEquation (ProVerifEquation _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "equation ";
        if (_node.vars.length > 0) {
            this.fullSpec += "forall ";
            boolean first = true;
            for (ProVerifVar var: _node.vars) {
                if (first)
                    first = false;
                else
                    this.fullSpec += ", ";
                this.fullSpec += var.name + ": " + var.type;
            }
            this.fullSpec += "; ";
        }
        this.fullSpec += _node.formula + ".";
    }

    protected void translateVar (ProVerifVar _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "free " + _node.name + ": " + _node.type;
        if (_node.priv)
            this.fullSpec += " [private]";
        this.fullSpec += ".";
    }

    protected void translateQueryAtt (ProVerifQueryAtt _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "query attacker(";
        if (_node.isNew)
           this.fullSpec += "new ";
        this.fullSpec += _node.name + ").";
    }

    protected void translateQueryEv (ProVerifQueryEv _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "query ";
        if (_node.vars.length > 0) {
            boolean first = true;
            for (ProVerifVar var: _node.vars) {
                if (first)
                    first = false;
                else
                    this.fullSpec += ", ";
                this.fullSpec += var.name + ": " + var.type;
            }
            this.fullSpec += "; ";
        }
        this.fullSpec += "event(" + _node.name + "()).";
    }

    protected void translateQueryEvinj (ProVerifQueryEvinj _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "query ";
        if (_node.vars.length > 0) {
            boolean first = true;
            for (ProVerifVar var: _node.vars) {
                if (first)
                    first = false;
                else
                    this.fullSpec += ", ";
                this.fullSpec += var.name + ": " + var.type;
            }
            this.fullSpec += "; ";
        }
        this.fullSpec += "inj-event(" + _node.ev1 + ") ==> inj-event(" + _node.ev2 + ").";
    }

    protected void translateEvDecl (ProVerifEvDecl _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "event " + _node.name + "(";
        boolean first = true;
        for (String arg: _node.args) {
            if (first)
                first = false;
            else
                this.fullSpec += ", ";
            this.fullSpec += arg;
        }
        this.fullSpec += ").";
    }

    protected void translateProcess (ProVerifProcess _node, int _alinea) {
        this.fullSpec += "\n\n" + printAlinea (_alinea);
        this.fullSpec += "let " + _node.name;
        if (_node.args.length > 0) {
            boolean first = true;
            this.fullSpec += " (";
            for (ProVerifVar var: _node.args) {
                if (first)
                    first = false;
                else
                    this.fullSpec += ", ";
                this.fullSpec += var.name + ": " + var.type;
            }
            this.fullSpec += ")";
        }
        this.fullSpec += " =";
        if (_node.next != null)
            this.translate (_node.next, _alinea+1);
        else
            this.fullSpec += " 0";
        this.fullSpec += ".";
    }

    protected void translateProcNew (ProVerifProcNew _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "new " + _node.name + "[]: " + _node.type + ";";
        if (_node.next == null) {
            this.fullSpec += "\n" + printAlinea (_alinea);
            this.fullSpec += "0";
        } else
            this.translate (_node.next, _alinea);
    }

    protected void translateProcIn (ProVerifProcIn _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "in (" + _node.channel + ", ";
	if (_node.vars.length>1){
	    this.fullSpec += "(";
	}
        boolean first = true;
        for (ProVerifVar var: _node.vars) {
            if (first)
                first = false;
            else
                this.fullSpec += ", ";
            this.fullSpec += var.name + ": " + var.type;
        }
        this.fullSpec += ")";
	if (_node.vars.length>1){
	    this.fullSpec += ")";
	}
        if (_node.next != null) {
            this.fullSpec += ";";
            this.translate (_node.next, _alinea);
        }
    }

    protected void translateProcCall (ProVerifProcCall _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += _node.name;
        if (_node.args.length > 0) {
            boolean first = true;
            this.fullSpec += " (";
            for (ProVerifVar var: _node.args) {
                if (first)
                    first = false;
                else
                    this.fullSpec += ", ";
                this.fullSpec += var.name;
            }
            this.fullSpec += ")";
        }

        if (_node.next != null) {
            this.fullSpec += ";";
            this.translate (_node.next, _alinea);
        }
    }

    protected void translateProcLet (ProVerifProcLet _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "let ";
        boolean first = true;
        if (_node.vars.length > 1)
            this.fullSpec += "(";
        for (ProVerifVar var: _node.vars) {
            if (first)
                first = false;
            else
                this.fullSpec += ", ";

            if (var.patternEqual)
                this.fullSpec += "=" + var.name;
            else
                this.fullSpec += var.name + ": " + var.type;
        }
        if (_node.vars.length > 1)
            this.fullSpec += ")";
        this.fullSpec += " = " + _node.value + " in";
        if (_node.next != null)
            this.translate (_node.next, _alinea);
        else {
            this.fullSpec += "\n" + printAlinea (_alinea);
            this.fullSpec += "0";
        }
    }
}

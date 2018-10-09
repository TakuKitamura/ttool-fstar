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
 * Class ProVerifSyntaxer
 * Creation: 13/09/2015
 * @version 1.0 13/09/2015
 * @author Florian LUGOU
 */
public abstract class ProVerifSyntaxer {

    public static final String DEC = "\t";
    public static final int DECLEN = DEC.length();

    protected String fullSpec;

    public ProVerifSyntaxer () {
        this.fullSpec = "";
    }

    protected static String printAlinea (int _alinea) {
        int i;
        String res = "";

        for (i=0; i<_alinea; i++)
            res += ProVerifSyntaxer.DEC;

        return res;
    }

    public void translate (ProVerifDeclaration _node, int _alinea) {
        if (_node != null)
            _node.translate (this, _alinea);
    }

    protected void translateSpec (ProVerifSpec _node, int _alinea) {
        for (ProVerifDeclaration decl: _node.declarations)
            this.translate (decl, _alinea);

        this.fullSpec += "\n\n";
        this.fullSpec += printAlinea (_alinea);
        this.fullSpec += "process";
        this.translate (_node.mainProcess.next, _alinea+1);
    }

    protected void translateComment (ProVerifComment _node, int _alinea) {
        if (_node.lines.isEmpty())
            return;

        this.fullSpec += "\n\n";
        this.fullSpec += printAlinea (_alinea);
        this.fullSpec += "(* ";

        boolean first = true;
        for (String l: _node.lines) {
            if (first)
                first = false;
            else {
                this.fullSpec += "\n";
                this.fullSpec += printAlinea (_alinea);
                this.fullSpec += " * ";
            }
            this.fullSpec += l;
        }

        this.fullSpec +=  " *)";
    }

    protected void translateProperty (ProVerifProperty _node, int _alinea) {
        this.fullSpec += "\n";
        this.fullSpec += printAlinea (_alinea);
        this.fullSpec += "set " + _node.prop + ".";
    }

    protected void translateSecrecyAssum (ProVerifSecrecyAssum _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "not " + _node.name + ".";
    }

    protected void translateProcRaw (ProVerifProcRaw _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += _node.raw;
        if (_node.next != null) {
            if (_node.nextOptional)
                this.fullSpec += ";";
            this.translate (_node.next, _alinea);
        }
    }

    protected void translateProcITE (ProVerifProcITE _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += "if " + _node.cond + " then";
        if (_node.next != null) {
            if (_node.elseInstr.next != null)
                this.fullSpec += " (";
            this.translate (_node.next, _alinea+1);
            if (_node.elseInstr.next != null)
                this.fullSpec += ")";
        }
        else
            this.fullSpec += " 0";
        if (_node.elseInstr.next != null) {
            this.fullSpec += "\n" + printAlinea (_alinea);
            this.fullSpec += "else";
            this.translate (_node.elseInstr.next, _alinea+1);
        }
    }

    protected void translateProcRawGlobing (ProVerifProcRawGlobing _node, int _alinea) {
        this.fullSpec += "\n" + printAlinea (_alinea);
        this.fullSpec += _node.before;
        if (_node.intraInstr.next != null)
            this.translate (_node.intraInstr.next, _alinea+1);
        else
            this.fullSpec += " 0 ";
        this.fullSpec += _node.after;

        if (_node.next != null) {
            this.fullSpec += ";";
            this.translate (_node.next, _alinea+1);
        }
    }

    protected void translateProcParallel (ProVerifProcParallel _node, int _alinea) {
        if (_node.instrs.size () == 1)
            this.translate (_node.instrs.get (0), _alinea);
        else if (_node.instrs.size () > 1) {
            this.fullSpec += "\n" + printAlinea (_alinea);
            this.fullSpec += "((";
            boolean first = true;
            for (ProVerifProcInstr instr: _node.instrs) {
                if (first)
                    first = false;
                else {
                    this.fullSpec += "\n" + printAlinea (_alinea);
                    this.fullSpec += ") | (";
                }

                this.translate (instr, _alinea+1);
            }
            this.fullSpec += "\n" + printAlinea (_alinea);
            this.fullSpec += "))";
        }

        if (_node.next != null) {
            this.fullSpec += ";";
            this.translate (_node.next, _alinea);
        }
    }

    protected abstract void translateConst (ProVerifConst _node, int _alinea);
    protected abstract void translateFunc (ProVerifFunc _node, int _alinea);
    protected abstract void translateReduc (ProVerifReduc _node, int _alinea);
    protected abstract void translateEquation (ProVerifEquation _node, int _alinea);
    protected abstract void translateVar (ProVerifVar _node, int _alinea);
    protected abstract void translateQueryAtt (ProVerifQueryAtt _node, int _alinea);
    protected abstract void translateQueryEv (ProVerifQueryEv _node, int _alinea);
    protected abstract void translateQueryEvinj (ProVerifQueryEvinj _node, int _alinea);
    protected abstract void translateEvDecl (ProVerifEvDecl _node, int _alinea);
    protected abstract void translateProcess (ProVerifProcess _node, int _alinea);
    protected abstract void translateProcNew (ProVerifProcNew _node, int _alinea);
    protected abstract void translateProcIn (ProVerifProcIn _node, int _alinea);
    protected abstract void translateProcLet (ProVerifProcLet _node, int _alinea);
    protected abstract void translateProcCall (ProVerifProcCall _node, int _alinea);

    private void makeSpec (ProVerifSpec _spec) {
        this.translate (_spec, 0);
    }

    public String getStringSpec (ProVerifSpec _spec) {
        if (_spec.modified) {
            this.makeSpec (_spec);
            _spec.modified = false;
        }

        return this.fullSpec;
    }
}

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

package tmltranslator;

import java.util.Arrays;
import java.util.Collection;

public class CPPCodeGenerationHelper {

    public static final String NORMALIZATION_SUFFIX = "_Norm";

    private static final String DOT_REPLACEMENT = "_DOT_";

    private static final String PATH_REPLACEMENT = "_PATH_";

    private static final String[] CPP_KEYWORDS = { "alignas", // (depuis C++11)
            "alignof", // (depuis C++11)
            "and", "and_eq", "asm", "auto", "bitand", "bitor", "bool", "break", "case", "catch", "char", "char16_t", // (depuis
                                                                                                                     // C++11)
            "char32_t", // (depuis C++11)
            "class", "compl", "const", "constexpr", // (depuis C++11)
            "const_cast", "continue", "decltype", // (depuis C++11)
            "default", "delete", "do", "double", "dynamic_cast", "else", "enum", "explicit", "export", "extern",
            "false", "float", "for", "friend", "goto", "if", "inline", "int", "long", "mutable", "namespace", "new",
            "noexcept", // (depuis
                        // C++11)
            "not", "not_eq", "nullptr", // (depuis C++11)
            "operator", "or", "or_eq", "private", "protected", "public", "register", "reinterpret_cast", "return",
            "short", "signed", "sizeof", "static", "static_assert", // (depuis C++11)
            "static_cast", "struct", "switch", "template", "this", "thread_local", // (depuis C++11)
            "throw", "true", "try", "typedef", "typeid", "typename", "union", "unsigned", "using", "virtual", "void",
            "volatile", "wchar_t", "while", "xor", "xor_eq" };

    private static final Collection<String> CPP_KEYWORDS_LIST = Arrays.asList(CPP_KEYWORDS);

    private CPPCodeGenerationHelper() {
    }

    public static String normalize(final String name) {
        return removeCKeywords(name).replaceAll("\\.", DOT_REPLACEMENT).replaceAll("::", PATH_REPLACEMENT);
    }

    public static String headerFileName(final String name) {
        return name + ".h";
    }

    public static String cppFileName(final String name) {
        return name + ".cpp";
    }

    public static String removeCKeywords(final String name) {
        if (CPP_KEYWORDS_LIST.contains(name)) {
            return name + NORMALIZATION_SUFFIX;
        }

        return name;
    }
}

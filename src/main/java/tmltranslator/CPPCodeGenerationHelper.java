package tmltranslator;

import java.util.Arrays;
import java.util.Collection;

public class CPPCodeGenerationHelper {
	
	public static final String NORMALIZATION_SUFFIX = "_Norm";
	
	private static final String DOT_REPLACEMENT = "_DOT_";
	
	private static final String PATH_REPLACEMENT = "_PATH_";

	private static final String[] CPP_KEYWORDS = {
		"alignas", // (depuis C++11)
		"alignof", // (depuis C++11)
		"and",
		"and_eq",
		"asm",
		"auto",
		"bitand",
		"bitor",
		"bool",
		"break",
		"case",
		"catch",
		"char",
		"char16_t", //(depuis C++11)
		"char32_t", // (depuis C++11)
		"class",
		"compl",
		"const",
		"constexpr", // (depuis C++11)
		"const_cast",
		"continue",
		"decltype", //(depuis C++11)
		"default",
		"delete",
		"do",
		"double",
		"dynamic_cast",
		"else",
		"enum",
		"explicit",
		"export",
		"extern",
		"false",
		"float",
		"for",
		"friend",
		"goto",
		"if",
		"inline",
		"int",
		"long",
		"mutable",
		"namespace",
		"new",
		"noexcept", //(depuis C++11)
		"not",
		"not_eq",
		"nullptr", // (depuis C++11)
		"operator",
		"or",
		"or_eq",
		"private",
		"protected",
		"public",
		"register",
		"reinterpret_cast",
		"return",
		"short",
		"signed",
		"sizeof",
		"static",
		"static_assert", // (depuis C++11)
		"static_cast",
		"struct",
		"switch",
		"template",
		"this",
		"thread_local", // (depuis C++11)
		"throw",
		"true",
		"try",
		"typedef",
		"typeid",
		"typename",
		"union",
		"unsigned",
		"using",
		"virtual",
		"void",
		"volatile",
		"wchar_t",
		"while",
		"xor",
		"xor_eq" };
	
	private static final Collection<String> CPP_KEYWORDS_LIST = Arrays.asList( CPP_KEYWORDS );
	
	private CPPCodeGenerationHelper() {
	}

	public static String normalize( final String name ) {
		return removeCKeywords( name ).replaceAll( "\\.", DOT_REPLACEMENT ).replaceAll( "::", PATH_REPLACEMENT );
	}
	
	public static String headerFileName( final String name ) {
		return name + ".h";
	}
	
	public static String cppFileName( final String name ) {
		return name + ".cpp";
	}

	public static String removeCKeywords( final String name ) {
		if ( CPP_KEYWORDS_LIST.contains( name ) ) {
			return name + NORMALIZATION_SUFFIX;
		}

		return name;
	}
}

package tmltranslator.tomappingsystemc2;

public class MergedCmdStr{
	String nextCmd;
	String funcs;
	String srcCmd;
	int num;
	MergedCmdStr(String iNextCommand, String iSrcCmd, int iNum){
		nextCmd=iNextCommand;
		srcCmd=iSrcCmd;
		num=iNum;
		funcs="";
	}
	MergedCmdStr(String iNextCommand, String iSrcCmd){
		this(iNextCommand, iSrcCmd, 0);
	}
}
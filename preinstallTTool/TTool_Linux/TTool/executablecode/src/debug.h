

#ifndef DEBUG_H
#define DEBUG_H

void activeDebug();
void unactiveDebug();

void debugThreeInts(char *msg, int value1, int value2, int value3);
void debugTwoInts(char *msg, int value1, int value2);
void debugLong(char *msg, long value);
void debugInt(char *msg, int value);
void debugMsg(char *msg);
void debug2Msg(char *name, char* msg);
void debugTime(char* msg, struct timespec *ts);

#endif



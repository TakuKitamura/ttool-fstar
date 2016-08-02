
#define MAP_A\

 .channel0 : { \
*(section_channel0)\
} > uram1\

 .channel1 : { \
*(section_channel1)\
} > uram1\

 .channel2 : { \
*(section_channel2)\
} > uram1\

 .channel3 : { \
*(section_channel3)\
} > uram1\

 .lock0 : { \
*(section_lock0)\
} > uram0\

 .lock1 : { \
*(section_lock1)\
} > uram0\

 .lock2 : { \
*(section_lock2)\
} > uram0\

 .lock3 : { \
*(section_lock3)\
} > uram0\

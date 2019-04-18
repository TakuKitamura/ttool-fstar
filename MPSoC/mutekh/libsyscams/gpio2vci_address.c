
#include "gpio2vci_address.h"

int get_address(char name[]) {
    if(strcmp(name, "vibration_sensor") == 0) {
        return 0xc0200000;
    } else {
        printf("ERROR getting address for cluster: \"%s\"\n", name);
        return -1;
    }
}

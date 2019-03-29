#include "gpio2vci_iface.h"

void write_gpio2vci(int data, char name[]) {
    int * wr_ptr;
    wr_ptr = (int*)get_address(name);
    *wr_ptr = data;
}

int read_gpio2vci(char name[]) {
    int * rd_ptr;
    rd_ptr = (int*)(get_address(name)+4);
    return *rd_ptr;
}

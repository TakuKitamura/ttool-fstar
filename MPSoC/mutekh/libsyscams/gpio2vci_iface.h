#ifndef GPIO2VCI_IFACE_H
#define GPIO2VCI_IFACE_H

#include "gpio2vci_address.h"

void write_gpio2vci(int data, char name[]);

int read_gpio2vci(char name[]);

#endif //GPIO2VCI_IFACE_H

#ifndef GPIO2VCI_IFACE_H
#define GPIO2VCI_IFACE_H

#include "gpio2vci_address.h"

void write_gpio2vci(int data, char name[]);
void write_gpio2vci_float(float data, char name[]);
//void write_gpio2vci_bool(bool data, char name[]);

int read_gpio2vci(char name[]);
float read_gpio2vci_float(char name[]);
//bool read_gpio2vci_bool(char name[]);

#endif //GPIO2VCI_IFACE_H

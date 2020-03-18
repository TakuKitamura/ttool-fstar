#include "gpio2vci_iface.h"


/* functions for writing data */

void write_gpio2vci(int data, char name[]) {
    int * wr_ptr;
    //wr_ptr = (int*)AMS_CLUSTER_WRITE;         //Address of the GPIO2VCI component.
    //wr_ptr = (int*)get_address(index);
    wr_ptr = (int*)get_address(name);
    *wr_ptr = data;
}

void write_gpio2vci_float(float data, char name[]) {
    int * wr_ptr;
    //wr_ptr = (int*)AMS_CLUSTER_WRITE;         //Address of the GPIO2VCI component.
    //wr_ptr = (int*)get_address(index);
    wr_ptr = (int*)get_address(name);
    *wr_ptr = data;
}


/*void write_gpio2vci_bool(bool data, char name[]) {
    int * wr_ptr;
    //wr_ptr = (int*)AMS_CLUSTER_WRITE;         //Address of the GPIO2VCI component.
    //wr_ptr = (int*)get_address(index);
    wr_ptr = (int*)get_address(name);
    *wr_ptr = data;
    }*/


/*void write_gpio2vci_ext(char name[], int data_int, int nb_params_int, float data_floar, int nb_params_float ) {
 
    int * wr_ptr_int;
    float * wr_ptr_float;
    for(i=0;i<nb_params_int;i++){
      wr_ptr = (int*)get_address(name);
      *wr_ptr = data;
    }
    for(i=0;i<nb_params_float;i++)
      {
	wr_ptr = (int*)get_address(name);
	*wr_ptr = data_float;
      }
      }*/

/* functions for reading data */


int read_gpio2vci(char name[]) {
    int * rd_ptr;
    rd_ptr = (int*)(get_address(name)+4);
    return *rd_ptr;
}

float read_gpio2vci_float(char name[]) {
    float * rd_ptr;
    rd_ptr = (int*)(get_address(name)+8);//size?
    return *rd_ptr;
}

/*bool read_gpio2vci_bool(char name[]) {
    bool * rd_ptr;
    rd_ptr = (int*)(get_address(name)+4);//+1?
    return *rd_ptr;
    }*/

/*void read_gpio2vci_ext(char name[], int* rd_ptr, int nb_params_int, float* rd_ptr_float, int nb_params_float ) {
    int i;
    for(i=0;i<nb_params_int;i++)
    rd_ptr* = (int*)(get_address(name)+4);
    for(i=0;i<nb_params_float;i++)
    rd_ptr_float* = (int*)(get_address(name)+4);
    return *rd_ptr;
    }*/


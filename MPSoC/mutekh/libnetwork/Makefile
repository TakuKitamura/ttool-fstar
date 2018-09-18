
DIR_CFLAGS = -D_HEXO_INTTYPES_DEPRECATED

objs += protocols.o packet.o if.o route.o dispatch.o

ifeq ($(CONFIG_NETWORK_FILTER), defined)
 objs += filter.o
endif

subdirs = protocols

ifeq ($(CONFIG_NETWORK_UDP), defined)
 subdirs += libudp
endif

ifeq ($(CONFIG_NETWORK_TCP), defined)
 subdirs += libtcp
endif

ifeq ($(CONFIG_NETWORK_SOCKET), defined)
 subdirs += libsocket
endif

ifeq ($(CONFIG_NETWORK_NFS), defined)
 subdirs += lwnfs
endif

ifeq ($(CONFIG_NETWORK_RARP), defined)
 subdirs += rarp
endif

ifeq ($(CONFIG_NETWORK_PING), defined)
 subdirs += ping
endif

ifeq ($(CONFIG_NETWORK_DHCLIENT), defined)
 subdirs += dhclient
endif

doc_headers = netinet/arp.h netinet/ether.h netinet/filter.h		\
netinet/icmp.h netinet/in.h netinet/ip.h netinet/sockaddr.h			\
netinet/socket.h netinet/sockios.h netinet/tcp.h netinet/udp.h		\
network/arp.h network/dhcp.h network/dispatch.h network/icmp.h		\
network/if.h network/ip.h network/libsocket.h network/libtcp.h		\
network/libudp.h network/nfs.h network/packet.h network/ping.h		\
network/protos.h network/route.h network/socket.h					\
network/socket_hexo.h network/socket_internals.h					\
network/socket_packet.h network/socket_raw.h network/socket_tcp.h	\
network/socket_udp.h network/tcp.h network/udp.h

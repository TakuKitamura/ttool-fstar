         i   h      ����������=��A�O�ı�R������y            u
objs=sam7.o

ifeq ($(CONFIG_ARCH_SIMPLE_OLIMEX_SAM7_EX256),defined)
objs += sam7_ex256_hw_init.o
endif
     i     V   �      �    ����J�����<�K�r�O��}�               h   h   J
ifeq ($(CONFIG_CPU_ARM_THUMB), defined)
sam7.o_CFLAGS = -mno-thumb
endif
     �     Y   �         �������ѝ�i�ӼiH��㹺                  E   9# ifeq ($(CONFIG_ARCH_SIMPLE_OLIMEX_SAM7_EX256),defined)
   b   h   # endif
         �   �        ������L�]�����/�s�X��)            x���O�*�-N�5�����LK-T�P�p��s�t�wr�����q�����u��v�5�w�025��IIM��KM�����m� 2&>�(�Q���Y445/%3�h�P���!��N�:
p� N�wv�qtV�U�����-�(�M�� ˤ6K    �        �     �   ����c!G�����Ū��@Tg�u<               �   �   
subdirs = drivers

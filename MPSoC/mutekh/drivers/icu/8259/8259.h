/*
    This file is part of MutekH.
    
    MutekH is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation; version 2.1 of the
    License.
    
    MutekH is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public
    License along with MutekH; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
    02110-1301 USA.

    Copyright Alexandre Becoulet <alexandre.becoulet@lip6.fr> (c) 2006

*/


#ifndef PIC_8259_H_
#define PIC_8259_H_

#define PIC_ICW1_INIT		0x10
#define PIC_ICW1_HAS_ICW4	0x01
#define PIC_ICW1_NO_SLAVE	0x02
#define PIC_ICW1_TRIGGER_MODE	0x08

#define PIC_ICW4_X86_MODE	0x01
#define PIC_ICW4_AUTO_IRQ_END	0x02
#define PIC_OCW2_EOI		0x20
#define PIC_OCW2_PRIO_NOP	0x40
#define PIC_OCW3_GET_ISR       	0x0b

#define PIC_SLAVE_CHAINED_IRQ	2 /* IRQ line on master used for slave */

#endif


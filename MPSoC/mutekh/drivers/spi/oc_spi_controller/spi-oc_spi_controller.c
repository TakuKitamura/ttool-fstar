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

    Copyright (c) Eric Guthmuller 2010

 */


#include <hexo/types.h>

#include <device/spi.h>
#include <device/icu.h>
#include <device/device.h>
#include <device/driver.h>

#include <hexo/iospace.h>
#include <mutek/mem_alloc.h>
#include <hexo/endian.h>
#include <mutek/printk.h>

#include <assert.h>

#include "spi-oc_spi_controller.h"
#include "spi-oc_spi_controller-private.h"

#define MCK 12500000
//#define OC_SPI_CTRL_DEBUG

static void spi_select_none(struct device_s *dev)
{
}

static void spi_select_normal(struct device_s *dev)
{
}



static CMD_HANDLER(spi_oc_spi_controller_read_rx_1byte)
{
  struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
  struct spi_oc_spi_controller_dev_config_s *config = pv->dev_config;
  uintptr_t registers = (uintptr_t)dev->addr[0];
  struct dev_spi_rq_s *rq = pv->current_request;
  ssize_t size = 1;

  if (pv->group) {
    size = pv->cur_length;
    ssize_t byte, idx;
    uint32_t word;

    for ( idx = size-1, byte = 0; byte < size; --idx, ++byte ) {
        word >>= 8;
        if ( (byte & 3) == 0 ) {
          word = endian_le32(cpu_mem_read_32(registers + SPI_OC_RX(byte/4)));
#ifdef OC_SPI_CTRL_DEBUG
          printk("SPI read_rx_1byte data : %x\n", word);
#endif
        }
        ((uint8_t *)pv->rx_ptr)[idx] = word & 0xFF;
    }
  } else {
    uint32_t data = cpu_mem_read_32(registers + SPI_OC_RX(0))&((1<<config[rq->device_id].bits_per_word)-1);
#ifdef OC_SPI_CTRL_DEBUG
    printk("SPI read_rx_1byte data : %x\n", data);
#endif
    *(uint8_t *)(pv->rx_ptr) = data;
  }
  pv->rx_ptr += pv->increment*size;
}

static CMD_HANDLER(spi_oc_spi_controller_read_rx_2bytes)
{
  struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
  struct spi_oc_spi_controller_dev_config_s *config = pv->dev_config;
  uintptr_t registers = (uintptr_t)dev->addr[0];
  struct dev_spi_rq_s *rq = pv->current_request;

  uint32_t data = cpu_mem_read_32(registers + SPI_OC_RX(0))&((1<<config[rq->device_id].bits_per_word)-1);
#ifdef OC_SPI_CTRL_DEBUG
  printk("SPI read_rx_2byte data : %x\n", data);
#endif
  *(uint16_t*)(pv->rx_ptr) = data;	
  pv->rx_ptr += pv->increment;
}

static CMD_HANDLER(spi_oc_spi_controller_void_rx)
{
}

static CMD_HANDLER(spi_oc_spi_controller_write_tx_1byte)
{
	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	uintptr_t registers = (uintptr_t)dev->addr[0];
  ssize_t size = 1;

  if (pv->group) {
    size = __MIN(16, pv->count);
    ssize_t byte, idx;
    uint32_t word = 0;

    for ( idx = size-1, byte = 0; byte < size; --idx, ++byte ) {
      word <<= 8;
      word |= ((uint8_t *)pv->tx_ptr)[byte];
      if ( (idx & 3) == 0 ) {
        cpu_mem_write_32(registers + SPI_OC_TX(idx/4), endian_le32(word));
#ifdef OC_SPI_CTRL_DEBUG
        data = cpu_mem_read_32(registers + SPI_OC_TX(idx/4));
        printk("SPI write_tx_1byte data : %x\n", data);
#endif
      }
    }

    uint32_t ctrl_reg = endian_le32(cpu_mem_read_32(registers + SPI_OC_CTRL));
    ctrl_reg = (ctrl_reg & ~SPI_OC_CHAR_LEN) | (size*8 & SPI_OC_CHAR_LEN);
    cpu_mem_write_32(registers + SPI_OC_CTRL, endian_le32(ctrl_reg));
  } else {
    uint32_t data = *(uint8_t *)pv->tx_ptr
      | pv->constant;

    // set the data
    cpu_mem_write_32(registers + SPI_OC_TX(0), data);
#ifdef OC_SPI_CTRL_DEBUG
    data = cpu_mem_read_32(registers + SPI_OC_TX(0));
    printk("SPI write_tx_1byte data : %x\n", data);
#endif
  }
  pv->tx_ptr += pv->increment*size;
  pv->cur_length = size;
	// go !
	cpu_mem_mask_set_32(registers + SPI_OC_CTRL, SPI_OC_GO_BSY);
	pv->count-=size;
}

static CMD_HANDLER(spi_oc_spi_controller_write_tx_2bytes)
{
	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	uintptr_t registers = (uintptr_t)dev->addr[0];

	uint32_t data = *(uint16_t*)(pv->tx_ptr)
//		| (pv->count == 0 ? (1<<24) : 0)
		;
	pv->tx_ptr += pv->increment;
	// set the data
	cpu_mem_write_32(registers + SPI_OC_TX(0), data);
#ifdef OC_SPI_CTRL_DEBUG
	data = cpu_mem_read_32(registers + SPI_OC_TX(0));
	printk("SPI write_tx_2bytes data : %x\n", data);
#endif
	// go !
	cpu_mem_mask_set_32(registers + SPI_OC_CTRL, SPI_OC_GO_BSY);
	pv->count--;
}

static CMD_HANDLER(spi_oc_spi_controller_pad_tx)
{
	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	uintptr_t registers = (uintptr_t)dev->addr[0];
  uint32_t size = 1;

  if (pv->group) {
    size = __MIN(16,pv->count);
    uint32_t max_word = (size-1) >> 2;
    ssize_t idx;
    for (idx = 0; idx<=max_word ; idx++)
      cpu_mem_write_32(registers + SPI_OC_TX(idx), pv->pad_ext);

    uint32_t ctrl_reg = endian_le32(cpu_mem_read_32(registers + SPI_OC_CTRL));
    ctrl_reg = (ctrl_reg & ~SPI_OC_CHAR_LEN) | (size*8 & SPI_OC_CHAR_LEN);
    cpu_mem_write_32(registers + SPI_OC_CTRL, endian_le32(ctrl_reg));
  } else {
    uint32_t data = pv->pad_byte;
#ifdef OC_SPI_CTRL_DEBUG
    printk("SPI pad_tx data : %x, base : %p\n", data, registers);
#endif
    // set the data
    cpu_mem_write_32(registers + SPI_OC_TX(0), data);
  }
  pv->cur_length = size;
  // go !
  cpu_mem_mask_set_32(registers + SPI_OC_CTRL, SPI_OC_GO_BSY);
  pv->count-=size;
}

static CMD_HANDLER(spi_oc_spi_controller_wait_value_rx)
{
  struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
  struct spi_oc_spi_controller_dev_config_s *config = pv->dev_config;
  uintptr_t registers = (uintptr_t)dev->addr[0];
  struct dev_spi_rq_s *rq = pv->current_request;

    	uint32_t data = cpu_mem_read_32(registers + SPI_OC_RX(0))&((1<<config[rq->device_id].bits_per_word)-1);
#ifdef OC_SPI_CTRL_DEBUG
	printk("SPI wait_value_rx data : %x\n", data);
#endif
	enum devspi_wait_value_answer_e ret = pv->wait_cb(dev, rq, data);
	switch (ret) {
	case DEV_SPI_VALUE_FAIL:
		pv->abort = 1;
		pv->count = 0;
		break;
	case DEV_SPI_VALUE_FOUND:
		pv->count = 0;
		break;
	case DEV_SPI_VALUE_RETRY:
		if ( pv->count == 0 )
			pv->abort = 1;
		break;
	}
}


static bool_t spi_oc_spi_controller_setup_command(struct device_s *dev, struct dev_spi_rq_cmd_s *cmd)
{
	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	struct spi_oc_spi_controller_dev_config_s *config = pv->dev_config;
  struct dev_spi_rq_s *rq = pv->current_request;
	uintptr_t registers = (uintptr_t)dev->addr[0];
	bool_t handled = 0;

	pv->abort = 0;

  // Reset length (in case of previous group transfers
  uint32_t ctrl_reg = endian_le32(cpu_mem_read_32(registers + SPI_OC_CTRL));
  ctrl_reg = (ctrl_reg & ~SPI_OC_CHAR_LEN) | (config[rq->device_id].bits_per_word & SPI_OC_CHAR_LEN);
  cpu_mem_write_32(registers + SPI_OC_CTRL, endian_le32(ctrl_reg));

	const char *ttype = "unknown";
	switch ( cmd->type ) {
	case DEV_SPI_DESELECT:
		ttype = "deselect";
		pv->group = 0;
		cpu_mem_write_32(registers + SPI_OC_SS, 0);
		handled = 1;
		break;
	case DEV_SPI_R_8:
		ttype = "read";
		pv->group = pv->groupable;
		pv->tx_handler = spi_oc_spi_controller_pad_tx;
		pv->rx_handler = spi_oc_spi_controller_read_rx_1byte;
		pv->increment = cmd->read.ptr_increment;
		pv->rx_ptr = (uintptr_t)cmd->read.data;
		pv->count = cmd->read.size;
		pv->pad_byte = cmd->read.padding;
		break;
	case DEV_SPI_W_8:
		ttype = "write";
		pv->group = pv->groupable;
		pv->tx_handler = spi_oc_spi_controller_write_tx_1byte;
		pv->increment = cmd->write.ptr_increment;
		pv->rx_handler = spi_oc_spi_controller_void_rx;
		pv->tx_ptr = (uintptr_t)cmd->write.data;
		pv->count = cmd->write.size;
		break;
	case DEV_SPI_RW_8:
		ttype = "read_write";
		pv->group = pv->groupable;
		pv->rx_handler = spi_oc_spi_controller_read_rx_1byte;
		pv->tx_handler = spi_oc_spi_controller_write_tx_1byte;
		pv->increment = cmd->read_write.ptr_increment;
		pv->rx_ptr = (uintptr_t)cmd->read_write.rdata;
		pv->tx_ptr = (uintptr_t)cmd->read_write.wdata;
		pv->count = cmd->read_write.size;
		break;
	case DEV_SPI_R_16:
		ttype = "read";
		pv->group = 0;
		pv->tx_handler = spi_oc_spi_controller_pad_tx;
		pv->rx_handler = spi_oc_spi_controller_read_rx_2bytes;
		pv->increment = cmd->read.ptr_increment;
		pv->rx_ptr = (uintptr_t)cmd->read.data;
		pv->count = cmd->read.size;
		pv->pad_byte = cmd->read.padding;
		break;
	case DEV_SPI_W_16:
		ttype = "write";
		pv->group = 0;
		pv->tx_handler = spi_oc_spi_controller_write_tx_2bytes;
		pv->increment = cmd->write.ptr_increment;
		pv->rx_handler = spi_oc_spi_controller_void_rx;
		pv->tx_ptr = (uintptr_t)cmd->write.data;
		pv->count = cmd->write.size;
		break;
	case DEV_SPI_RW_16:
		ttype = "read_write";
		pv->group = 0;
		pv->rx_handler = spi_oc_spi_controller_read_rx_2bytes;
		pv->tx_handler = spi_oc_spi_controller_write_tx_2bytes;
		pv->increment = cmd->read_write.ptr_increment;
		pv->rx_ptr = (uintptr_t)cmd->read_write.rdata;
		pv->tx_ptr = (uintptr_t)cmd->read_write.wdata;
		pv->count = cmd->read_write.size;
		break;
	case DEV_SPI_SET_CONSTANT:
		ttype= "set_constant";
		pv->group = 0;
		pv->constant = cmd->constant.data; 
		handled = 1;
		break;
	case DEV_SPI_WAIT_VALUE:
		ttype= "wait_byte_value";
		pv->group = 0;
		pv->tx_handler = spi_oc_spi_controller_pad_tx;
		pv->rx_handler = spi_oc_spi_controller_wait_value_rx;
		pv->pad_byte = cmd->wait_value.padding;
		pv->wait_cb = cmd->wait_value.callback;
		pv->count = cmd->wait_value.timeout;
		break;
	case DEV_SPI_PAD_UNSELECTED:
		ttype= "pad_unselected";
		pv->group = pv->groupable;
		spi_select_none(dev);
		goto pad;
	case DEV_SPI_PAD:
		ttype= "pad";
		pv->group = pv->groupable;
	pad:
		pv->tx_handler = spi_oc_spi_controller_pad_tx;
		pv->rx_handler = spi_oc_spi_controller_void_rx;
		pv->pad_byte = cmd->pad.padding;
		pv->count = cmd->pad.size;
		break;
	}
#ifdef OC_SPI_CTRL_DEBUG
	printk("Spi new command %s\n", ttype);
#endif
  
  pv->pad_ext = (pv->pad_byte & 0xFF) << 24 |
                (pv->pad_byte & 0xFF) << 16 |
                (pv->pad_byte & 0xFF) << 8  |
                (pv->pad_byte & 0xFF) << 0  ;

	return handled;
}

static inline
struct dev_spi_rq_s *spi_oc_spi_controller_get_next_req(struct device_s *dev)
{
  struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
  LOCK_SPIN_IRQ(&dev->lock);
  if(pv->current_request == NULL)
    pv->current_request = dev_spi_queue_pop(&pv->queue);
  LOCK_RELEASE_IRQ(&dev->lock);
  return pv->current_request;
}

static
struct dev_spi_rq_cmd_s *spi_oc_spi_controller_get_next_cmd(struct device_s *dev)
{
	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	struct spi_oc_spi_controller_dev_config_s *config = pv->dev_config;
	uintptr_t registers = (uintptr_t)dev->addr[0];

	struct dev_spi_rq_s *rq;

	while ((rq = spi_oc_spi_controller_get_next_req(dev))) {
		if ( pv->cur_cmd == (size_t)-1 ) {
			uint32_t select = (0x1 << rq->device_id);
			uint32_t divider = 0;
			cpu_mem_write_32(registers + SPI_OC_SS, select);
			pv->constant = 0;
			pv->cur_cmd = 0;
			// set the divider
			cpu_mem_write_32(registers + SPI_OC_DIVIDER, config[rq->device_id].dividers & SPI_OC_DIVIDER_M);
			// set the default length
			uint32_t mode_rx = (config[rq->device_id].modes & 0x1) ? SPI_OC_RX_NEG : 0;
			uint32_t mode_tx = (config[rq->device_id].modes & 0x1) ? 0 : SPI_OC_TX_NEG;
			uint32_t keep_cs = (config[rq->device_id].keep_cs & 0x1) ? SPI_OC_ASS : 0;
			uint32_t ctrl_reg = cpu_mem_read_32(registers + SPI_OC_CTRL);
			ctrl_reg = (ctrl_reg & ~SPI_OC_CHAR_LEN) | (config[rq->device_id].bits_per_word&SPI_OC_CHAR_LEN);
			ctrl_reg = (ctrl_reg & ~SPI_OC_RX_NEG) | mode_rx;
			ctrl_reg = (ctrl_reg & ~SPI_OC_TX_NEG) | mode_tx;
			ctrl_reg = (ctrl_reg & ~SPI_OC_ASS) | keep_cs;
			cpu_mem_write_32(registers + SPI_OC_CTRL, ctrl_reg);
      pv->groupable = (config[rq->device_id].bits_per_word == 8);
#ifdef OC_SPI_CTRL_DEBUG
			divider = cpu_mem_read_32(registers + SPI_OC_DIVIDER);
			ctrl_reg = cpu_mem_read_32(registers + SPI_OC_CTRL);
			printk("SPI request, Setting SS 0x%x , CTRL 0x%x and Divisor 0x%x registers\n",select,ctrl_reg,divider);
#endif
		} else {
			// A command terminated, so let's handle the cleanup
			struct dev_spi_rq_cmd_s *last_cmd = &rq->command[pv->cur_cmd];
			pv->constant = 0;

			if ( last_cmd->type == DEV_SPI_PAD_UNSELECTED ) {
				spi_select_normal(dev);
			}

			if ( pv->abort ) {
        rq->callback(rq->pvdata, rq, 1);
        LOCK_SPIN_IRQ(&dev->lock);
        pv->current_request = NULL;
        pv->cur_cmd = (size_t)-1;
        LOCK_RELEASE_IRQ(&dev->lock);
				continue;
			}

			if ( pv->cur_cmd == rq->command_count - 1 ) {
        rq->callback(rq->pvdata, rq, 0);
        LOCK_SPIN_IRQ(&dev->lock);
        pv->current_request = NULL;
        pv->cur_cmd = (size_t)-1;
        LOCK_RELEASE_IRQ(&dev->lock);
				continue;
			}

			pv->cur_cmd++;
		}
		struct dev_spi_rq_cmd_s *cmd = &rq->command[pv->cur_cmd];

		if ( spi_oc_spi_controller_setup_command(dev, cmd) )
			continue;
		
/* 		printk("Cmd rx: %p, tx: %p, inc: %d, rptr: %p, wptr: %p, count: %d, perm: %p\n", */
/* 			   pv->rx_handler, pv->tx_handler,  */
/* 			   pv->increment, */
/* 			   pv->rx_ptr, pv->tx_ptr, */
/* 			   pv->count, pv->permanent); */

		return &rq->command[pv->cur_cmd];
	}
	return NULL;
}

DEVSPI_REQUEST(spi_oc_spi_controller_request)
{
	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
  bool_t queue_empty;
  LOCK_SPIN_IRQ(&dev->lock);
 
  if((dev_spi_queue_head(&pv->queue) == NULL) &&
     (pv->current_request == NULL)){
    queue_empty = 1;
    pv->current_request = rq;
  } else {
    queue_empty = 0;
    dev_spi_queue_pushback(&pv->queue, rq);
  }
 
  LOCK_RELEASE_IRQ(&dev->lock);
 
 
  if ( queue_empty ) {
    struct dev_spi_rq_cmd_s *cmd = spi_oc_spi_controller_get_next_cmd(dev);
    assert( cmd );
    pv->tx_handler(dev);
  }
}

DEV_IRQ(spi_oc_spi_controller_irq)
{
    struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	uintptr_t registers = (uintptr_t)dev->addr[0];

	// reset interrupt
	cpu_mem_read_32(registers + SPI_OC_CTRL);

	pv->rx_handler(dev);
	if ( pv->count == 0 ) {
		struct dev_spi_rq_cmd_s *cmd = spi_oc_spi_controller_get_next_cmd(dev);

		if ( !cmd ) {
			return 0;
		}
	}
	pv->tx_handler(dev);

	return 0;
}

DEVSPI_SET_BAUDRATE(spi_oc_spi_controller_set_baudrate)
{
    	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	struct spi_oc_spi_controller_dev_config_s *config = pv->dev_config;

	if ( device_id >= pv->lun_count )
		return (uint32_t) -1;
        uint32_t i;
	for(i=0; i < (1<<16); i++){
	  if((i+1)*br*2>MCK)
	    break;	
	}
/*
	uint32_t divisor = MCK/(br*2);
	if ( MCK/divisor > br*2 )
		divisor++;
	if ( divisor > 65535 )
		divisor = 65535;
        uint32_t return_value = MCK/(divisor*2);
	if ( divisor != 0 )
		divisor--;
*/
uint32_t return_value = br;
/* 	dprintk("Setting CSR[%d]'s baudrate, asked %d, got %d\n", */
/* 		   device_id, br, MCK/divisor); */

/* 	dprintk("Setting CSR[%d] to %p\n", device_id, new_data); */

	// We save the divider for this device
	//pv->dividers[device_id]=divisor;
	config[device_id].dividers=i;

	return return_value;
}

DEVSPI_SET_DATA_FORMAT(spi_oc_spi_controller_set_data_format)
{
	struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;
	struct spi_oc_spi_controller_dev_config_s *config = pv->dev_config;

	if ( device_id >= pv->lun_count )
		return ERANGE;

	if ( bits_per_word > 16 || bits_per_word < 8 )
		return ERANGE;

	// We save the number of bits per word for this device
	config[device_id].bits_per_word = bits_per_word ;

	// We save the mode
	config[device_id].modes=spi_mode;
	if(spi_mode > 1){
		printk("SPI Controller, unsupported mode, mode = %i\n", spi_mode);
	}
	
	// We save the keep_cs
	config[device_id].keep_cs=keep_cs_active;

	return 0;
}

#ifdef CONFIG_DRIVER_ENUM_FDT
static const struct driver_param_binder_s spi_oc_spi_controller_param_binder[] =
{
	PARAM_BIND(struct spi_oc_spi_controller_param_s, lun_count, PARAM_DATATYPE_INT),
	{ 0 }
};

static const struct devenum_ident_s	spi_oc_spi_controller_ids[] =
{
	DEVENUM_FDTNAME_ENTRY("oc_spi_controller", sizeof(struct spi_oc_spi_controller_param_s), spi_oc_spi_controller_param_binder),
	{ 0 }
};
#endif

const struct driver_s   spi_oc_spi_controller_drv =
{
    .class      = device_class_spi,
#ifdef CONFIG_DRIVER_ENUM_FDT
    .id_table   = spi_oc_spi_controller_ids,
#endif
    .f_init     = spi_oc_spi_controller_init,
    .f_cleanup  = spi_oc_spi_controller_cleanup,
    .f_irq      = spi_oc_spi_controller_irq,
	.f.spi = {
		.f_request = spi_oc_spi_controller_request,
		.f_set_baudrate = spi_oc_spi_controller_set_baudrate,
		.f_set_data_format = spi_oc_spi_controller_set_data_format,
	},
};

#ifdef CONFIG_DRIVER_ENUM_FDT
REGISTER_DRIVER(spi_oc_spi_controller_drv);
#endif

DEV_INIT(spi_oc_spi_controller_init)
{
	struct spi_oc_spi_controller_context_s   *pv;
	struct spi_oc_spi_controller_param_s *param = params;
	uintptr_t registers = (uintptr_t)dev->addr[0];


    printk("SPI : Initialize SPI controller, base %p\n", registers);

    printk("%s intok: %d\n", __FUNCTION__, cpu_is_interruptible());
    printk("Status: %d\n", cpu_mips_mfc0(CPU_MIPS_STATUS, 0));
    printk("Cause: %d\n", cpu_mips_mfc0(CPU_MIPS_CAUSE, 0));

	dev->drv = &spi_oc_spi_controller_drv;

	if ( param->lun_count > 8 ) {
		printk("SPI-OC_SPI_CONTROLLER: Invalid lun count: %d\n", param->lun_count);
		return -1;
	}

	/* allocate private driver data */
	pv = mem_alloc(sizeof(*pv), (mem_scope_sys));

	if (!pv)
		return -1;

	dev->drv_pv = pv;

	pv->lun_count = param->lun_count;
	pv->cur_cmd = (size_t)-1;
  pv->current_request = NULL;

	pv->dev_config = mem_alloc(sizeof(struct spi_oc_spi_controller_dev_config_s) * (param->lun_count), (mem_scope_sys));
	if (!pv->dev_config)
		return -1;

	dev_spi_queue_init(&pv->queue);

	dev_icu_sethndl(dev->icudev, dev->irq, spi_oc_spi_controller_irq, dev);
	dev_icu_enable(dev->icudev, dev->irq, 1, 0 );

	cpu_mem_mask_set_32(registers+SPI_OC_CTRL, SPI_OC_IE);

  printk("SPI : Initialize SPI controller done\n");
	return 0;
}

DEV_CLEANUP(spi_oc_spi_controller_cleanup)
{
    struct spi_oc_spi_controller_context_s *pv = dev->drv_pv;

    DEV_ICU_UNBIND(dev->icudev, dev, dev->irq, spi_oc_spi_controller_irq);

    mem_free(pv);
}


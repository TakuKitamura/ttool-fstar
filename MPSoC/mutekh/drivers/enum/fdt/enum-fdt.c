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

    Copyright (c) 2009, Nicolas Pouillon <nipo@ssji.net>
*/


#include <hexo/types.h>
#include <mutek/mem_alloc.h>
#include <hexo/local.h>
#include <hexo/segment.h>

#include <device/enum.h>
#include <device/icu.h>
#include <device/device.h>
#include <device/driver.h>

#include <hexo/lock.h>
#include <hexo/interrupt.h>

#include <string.h>

#include <fdt/reader.h>

#include "enum-fdt.h"
#include "enum-fdt-private.h"



struct device_s *enum_fdt_get_at_offset(struct device_s *dev, uint32_t offset)
{
	CONTAINER_FOREACH_NOLOCK(device_list, CLIST, &dev->children, {
			struct enum_pv_fdt_s *enum_pv = item->enum_pv;
			if ( enum_pv->offset == offset )
				return item;
		});
	return NULL;
}

struct device_s *enum_fdt_get_phandle(struct device_s *dev, uint32_t phandle)
{
	CONTAINER_FOREACH_NOLOCK(device_list, CLIST, &dev->children, {
			struct enum_pv_fdt_s *enum_pv = item->enum_pv;
			if ( enum_pv->phandle == phandle ) {
                enum_fdt_register_one(dev, item);
				return item;
            }
		});
	return NULL;
}

void enum_fdt_children_init(struct device_s *dev)
{
	dprintk("registering drivers\n");
	CONTAINER_FOREACH_NOLOCK(device_list, CLIST, &dev->children, {
			struct enum_pv_fdt_s *enum_pv;
			enum_pv = item->enum_pv;
			dprintk(" registering driver for %s\n", enum_pv->device_path);
			enum_fdt_register_one(dev, item);
		});
}

DEVENUM_LOOKUP(enum_fdt_lookup)
{
	struct enum_fdt_context_s *pv = dev->drv_pv;

	CONTAINER_FOREACH(fdt_node, CLIST, &pv->devices, {
			dprintk("[%s] ", item->device_path);
			if ( !strcmp(item->device_path, path) ) {
                enum_fdt_register_one(dev, item->dev);
				return item->dev;
            }
            char path2[ENUM_FDT_PATH_MAXLEN];
            strncpy(path2, item->device_path+1, ENUM_FDT_PATH_MAXLEN);
            char *foo;
            while ( (foo = strchr(path2, '/') ) )
                *foo = '-';
			if ( !strcmp(path, path2) ) {
                enum_fdt_register_one(dev, item->dev);
				return item->dev;
            }
		});
	return NULL;
}

DEVENUM_INFO(enum_fdt_info)
{
    if ( child->parent != dev )
        return -EINVAL;

    struct enum_pv_fdt_s *enum_pv = child->enum_pv;
    strncpy(info->path, enum_pv->device_path+1, DEV_ENUM_MAX_PATH_LEN);
    char *foo;
    while ( (foo = strchr(info->path, '/') ) )
        *foo = '-';
    return 0;
}

error_t enum_fdt_register_one(struct device_s *dev, struct device_s *item)
{
	struct enum_pv_fdt_s *enum_pv = item->enum_pv;

	/* ignore already configured devices */
	if (item->drv != NULL)
		return 0;

	const struct driver_s *drv = driver_get_matching_fdtname(enum_pv->device_type);

	if ( drv == NULL ) {
		dprintk("No driver for %s\n", enum_pv->device_type);
        return -ENOTSUP;
	}

	return enum_fdt_use_drv(dev, item, drv);
}

struct device_s *
enum_fdt_icudev_for_cpuid(struct device_s *dev, cpu_id_t id)
{
	struct enum_fdt_context_s *pv = dev->drv_pv;
    struct device_s *rdev = NULL;

    LOCK_SPIN_IRQ(&dev->lock);

	dprintk("Looking up cpu icudev for cpuid %d... ", id);
	CONTAINER_FOREACH(fdt_node, CLIST, &pv->devices, {
			dprintk("[%s %s/%d] ", item->device_type, item->device_path, item->cpuid);
			if ( !strncmp(item->device_type, "cpu:", 4)
				 && item->cpuid == id ) {
				dprintk("OK\n");
                enum_fdt_register_one(dev, item->dev);
				rdev = item->dev;
			}
		});
    if ( rdev == NULL )
	dprintk("not found\n");

    LOCK_RELEASE_IRQ(&dev->lock);

	return rdev;
}

static FDT_ON_NODE_ENTRY_FUNC(wake_node_entry)
{
	void ***entry_ptr = priv;

    const void *value = NULL;
    size_t len;

    if ( fdt_reader_has_prop(state, "boot_vector_pointer", &value, &len ) ) {
        fdt_parse_sized( 1, value, sizeof(*entry_ptr), entry_ptr );
    }

    return 0;
}

error_t enum_fdt_wake_cpuid(struct device_s *dev, cpu_id_t id, void *entry)
{
	struct enum_fdt_context_s *pv = dev->drv_pv;
    struct device_s *rdev = NULL;

    LOCK_SPIN_IRQ(&dev->lock);
	dprintk("Looking up cpu icudev for cpuid %d... ", id);
	CONTAINER_FOREACH(fdt_node, CLIST, &pv->devices, {
			dprintk("[%s %s/%d] ", item->device_type, item->device_path, item->cpuid);
			if ( !strncmp(item->device_type, "cpu:", 4)
				 && item->cpuid == id ) {
				dprintk("OK\n");
				rdev = item->dev;
			}
		});
    LOCK_RELEASE_IRQ(&dev->lock);

    if ( rdev == NULL ) {
        dprintk("not found\n");
        return -ENOENT;
    }

    void **entry_ptr = NULL;
    struct enum_pv_fdt_s *enum_pv = rdev->enum_pv;
	struct fdt_walker_s walker = {
		.priv = &entry_ptr,
		.on_node_entry = wake_node_entry,
		.on_node_leave = NULL,
		.on_node_prop = NULL,
		.on_mem_reserve = NULL,
	};

	error_t err = fdt_walk_blob_from(pv->blob, &walker, enum_pv->offset);
    if ( err )
        return err;

    if ( entry_ptr ) {
        *entry_ptr = entry;
        return 0;
    }

    return -ENOENT;
}

/*
 * device open operation
 */

const struct driver_s	enum_fdt_drv =
{
	.class		= device_class_enum,
	.f_init		= enum_fdt_init,
	.f_cleanup		= enum_fdt_cleanup,
	.f.denum = {
		.f_lookup = enum_fdt_lookup,
		.f_info = enum_fdt_info,
//		.f_register		= enum_fdt_register,
	}
};

static void *clone_blob( void *blob )
{
	size_t size = fdt_get_size(blob);
	if ( blob == NULL || !size )
		return NULL;
	void *b2 = mem_alloc(size, (mem_scope_sys));
	if ( b2 )
		memcpy(b2, blob, size);
	return b2;
}

extern struct device_s *console_dev;	

DEV_INIT(enum_fdt_init)
{
	struct enum_fdt_context_s *pv;

	dev->drv = &enum_fdt_drv;

	/* allocate private driver data */
	pv = mem_alloc(sizeof(*pv), (mem_scope_sys));

	if (!pv)
		return -1;

	pv->blob = clone_blob(params);
	dprintk("blob cloned from %p to %p\n", params, pv->blob);
	if ( !pv->blob ) {
		mem_free(pv);
		return -1;
	}

	fdt_node_init(&pv->devices);
	pv->console_path = NULL;

	dev->drv_pv = pv;

	dprintk("creating children\n");
	enum_fdt_create_children(dev);

/* 	dprintk("registering drivers\n"); */
/* 	CONTAINER_FOREACH(device_list, CLIST, &dev->children, { */
/* 			struct enum_pv_fdt_s *enum_pv; */
/* 			enum_pv = item->enum_pv; */
/* 			dprintk(" registering driver for %s\n", enum_pv->device_path); */
/* 			enum_fdt_register_one(dev, item); */
/* 		}); */

	return 0;
}


/*
 * device close operation
 */

DEV_CLEANUP(enum_fdt_cleanup)
{
	struct enum_fdt_context_s	*pv = dev->drv_pv;

	mem_free(pv->blob);
	mem_free(pv);
}


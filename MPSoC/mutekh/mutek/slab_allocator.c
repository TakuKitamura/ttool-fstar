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

    Copyright Dimitri Refauvelet <dimitri.refauvelet@lip6.fr> (c) 2010
*/

#include <mutek/slab_allocator.h>


#ifdef CONFIG_MUTEK_SLAB_GLOBAL

slab_list_root_t slab_root;
lock_t slab_lock;

#endif

struct slab_allocator_header_s* slab_init(size_t size,
                                          uint_fast32_t count,
                                          enum mem_scope_e scope,
                                          uint8_t* name)
{
  if (size == 0 || count == 0)
    return NULL;

  struct slab_allocator_header_s* slab = mem_alloc(slab_hdr_size, scope);
  if (slab == NULL)
    return NULL;

  lock_init(&slab->lock);
  pool_list_init(&slab->pool_root);

  slab->size = size;
  slab->scope = scope;
  slab->count = count;
 #ifdef CONFIG_MUTEK_SLAB_NAME
  slab->name = name;
 #endif

  slab->current = pool_init(size, count, scope);
  if (slab->current == NULL )
    {
      mem_free(slab);
      return NULL;
    }

  pool_list_push(&slab->pool_root, slab->current);

  return slab;
}

void *slab_pop(struct slab_allocator_header_s* slab)
{
  lock_spin(&slab->lock);

  if (slab->current == NULL)
    {
      slab->current = pool_init(slab->size, slab->count, slab->scope);
      if (slab->current == NULL )
        return NULL;
      else 
        pool_list_push(&slab->pool_root, slab->current);
    }

  void * block = slab->current->head;

  slab->current->head = (void*)*(uintptr_t*)block;

  /*If pool is full, look for a non full pool, and if there is no one, create ones*/
  if (slab->current->head == NULL)
    {
      //TODO change with CONTAINER_while if exist
      CONTAINER_FOREACH(pool_list, CLIST, &slab->pool_root,
      {
        if (item->head != NULL)
        {
          slab->current = item;
          return block;
        }
      });

      //All pool are full, create a new one
      slab->current = NULL;
    }
  
  lock_release(&slab->lock);

  return block;
}

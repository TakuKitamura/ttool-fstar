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

/**
 * @file
 * @module{Mutek}
 * @short Slab allocation stuff
 */
/*
Slab is an allocator of fixed size memory space. A slab contains
pools, which contains elementary memory space. At init, a pool is
created and associated to the slab. If all pool are full, a new pool
is created.
*/


#ifndef SLAB_ALLOCATOR_H_ 
#define SLAB_ALLOCATOR_H_

#include <mutek/mem_alloc.h>

#include <hexo/types.h>
#include <hexo/error.h>
#include <hexo/lock.h>
#include <string.h>

#include <hexo/gpct_platform_hexo.h>
#include <gpct/cont_clist.h>


struct slab_pool_header_s
{
  CONTAINER_ENTRY_TYPE(CLIST) pool_entry;
  void* head; 
};

CONTAINER_TYPE(pool_list, CLIST, struct slab_pool_header_s, pool_entry);

struct slab_allocator_header_s
{
 #ifdef CONFIG_MUTEK_SLAB_GLOBAL
  CONTAINER_ENTRY_TYPE(CLIST) global_entry;
 #endif
  lock_t lock;

  pool_list_root_t pool_root;

  struct slab_pool_header_s *current;

  size_t size;
  uint_fast32_t count;
  enum mem_scope_e scope;

 #ifdef CONFIG_MUTEK_SLAB_NAME
  uint8_t * name;
 #endif
};

typedef struct slab_allocator_header_s * slab_t;

static const size_t pool_hdr_size = sizeof (struct slab_pool_header_s);
static const size_t slab_hdr_size = sizeof (struct slab_allocator_header_s);


CONTAINER_FUNC(pool_list, CLIST, static inline, pool_list, pool_entry);

struct slab_allocator_header_s* slab_init(size_t size,
                                          uint_fast32_t count,
                                          enum mem_scope_e scope,
                                          uint8_t* name);


 #ifdef CONFIG_MUTEK_SLAB_GLOBAL
CONTAINER_TYPE(slab_list, CLIST, struct slab_allocator_header_s, global_entry);

extern slab_list_root_t slab_root;
extern lock_t slab_lock;

CONTAINER_FUNC(slab_list, CLIST, static inline, slab_list, global_entry);

/**
   @this init the global slab list.
*/
static inline
void slab_global_init()
{
  slab_list_init(&slab_root);
  lock_init(&slab_lock);
}

/**
   @this register the slab in global slab list.
*/
static inline
void slab_global_register(struct slab_allocator_header_s* slab)
{
  lock_spin(&slab_lock);
  slab_list_push(&slab_root, slab); 
  lock_release(&slab_lock);
}

/**
   @this unregister the slab in global slab list.
*/
static inline
void slab_global_unregister(struct slab_allocator_header_s* slab)
{
  lock_spin(&slab_lock);
  slab_list_remove(&slab_root, slab);
  lock_release(&slab_lock);
}

/**
   @this create a slab and register it in global slab list.
   @param size Size of an elementary block of the slab.
   @param count Count of elementary block in a pool.
   @param scope Scope used for pool memory allocation.
   @return the new slab pool
*/
static inline
struct slab_allocator_header_s* slab_global_create(size_t size,
                                                   uint_fast32_t count,
                                                   enum mem_scope_e scope,
                                                   uint8_t* name)
{
  struct slab_allocator_header_s* slab = slab_init(size, count, scope, name);
  if (slab == NULL)
    return NULL;
  
  slab_global_register(slab);

  return slab;
}
 #endif/*CONFIG_MUTEK_SLAB_GLOBAL*/


/**
@internal
 */
static inline
struct slab_pool_header_s* pool_init(size_t size, uint_fast32_t count, enum mem_scope_e scope)
{
  void *pool_memory = mem_alloc(pool_hdr_size + (count * size) , scope);
  if (pool_memory == NULL)
    return NULL;

  struct slab_pool_header_s* pool = (void*)((uintptr_t)pool_memory + (count * size));
  
  pool->head = (void*)pool_memory;
  
  uint_fast16_t i = 0;
  uintptr_t* c;
  for (i=0; i<count-1; i++)
    {
      c = (void*)((uintptr_t)pool->head + i * size);
      *c = (uintptr_t)pool->head + (i+1) * size;
    }
  c = (void*)( (uintptr_t)pool->head + (count-1) * size );
  *c = (uintptr_t)NULL; 

  return pool;
}

/**
@internal
 */
static inline
bool_t pool_is_empty(struct slab_pool_header_s* pool, uint_fast8_t count)
{
  void* current = pool->head;
  while (current != NULL && count > 0)
    {
      count --;
      current = (void*)*(uintptr_t*)current;
    }
  if (count == 0)
    return 1;

  return 0;
}

/**
@internal
 */
static inline 
void pool_push(struct slab_pool_header_s* pool, void *block)
{
  *(uintptr_t*)block = (uintptr_t)pool->head;
  pool->head = block;
}

/**
@internal
 */
static inline
bool_t pool_contains(struct slab_pool_header_s* pool, size_t size, uint_fast32_t count, void *block)
{
  if ((uintptr_t)block > ((uintptr_t)pool - count*size) &&
      (uintptr_t)block < (uintptr_t)pool )
    return 1;
  return 0;
}


/**
@this creates a new slab and allocate a first pool. 
@param size Size of an elementary block of the slab.
@param count Count of elementary block in a pool.
@param scope Scope used for pool memory allocation.
@return the new slab pool
*/
struct slab_allocator_header_s* slab_init(size_t size,
                                          uint_fast32_t count,
                                          enum mem_scope_e scope,
                                          uint8_t* name);
/**
@this destroys the given slab pool: the pool is deleted from the
global pool list and memory space use by the pool is free. 
*/
static inline
void slab_destroy(struct slab_allocator_header_s* slab)
{
  lock_spin(&slab->lock);
  CONTAINER_FOREACH(pool_list, CLIST, &slab->pool_root,
  {
    pool_list_remove(&slab->pool_root, item);
    mem_free(item);
  });
  lock_release(&slab->lock);
  mem_free(slab);
}

/**
@this look for empty pool and destroy it. Use sparingly. 
*/
static inline
void slab_clean(struct slab_allocator_header_s* slab)
{
  lock_spin(&slab->lock);
  CONTAINER_FOREACH(pool_list, CLIST, &slab->pool_root,
  {
    if (pool_is_empty(item, slab->count))
      {
        pool_list_remove(&slab->pool_root, item);
        mem_free(item);
      }
  });  
  lock_release(&slab->lock);
}

/**
@this return a free memory elementary block.
*/
void *slab_pop(struct slab_allocator_header_s* slab);

/**
@this set the given memory elementary block like free.
*/
static inline
void slab_push(struct slab_allocator_header_s* slab, void *block)
{
  lock_spin(&slab->lock);
  CONTAINER_FOREACH(pool_list, CLIST, &slab->pool_root,
  {
    if (pool_contains(item, slab->size, slab->count, block))
      {
        pool_push(item, block);
        return;
      }
  });
  lock_release(&slab->lock);
}

/**
@this return the elementary block size of the slab.
*/
static inline
size_t slab_getsize(struct slab_allocator_header_s* slab)
{
  return slab->size;
}

/**
@this set the scope use for pool memory allocation.
 */
static inline
void slab_set_scope(struct slab_allocator_header_s* slab, enum mem_scope_e scope)
{
  lock_spin(&slab->lock);
  slab->scope = scope;
  lock_release(&slab->lock);
}

/**
@this return the scope use for pool memory allocation.
 */
static inline
enum mem_scope_e slab_get_scope(struct slab_allocator_header_s* slab)
{
  return slab->scope;
}

 #ifdef CONFIG_MUTEK_SLAB_NAME

  #ifdef CONFIG_MUTEK_SLAB_GLOBAL
/**
@this return the slab associate to the given name.
*/
static inline
struct slab_allocator_header_s *slab_getbyname(uint8_t * name)
{
  lock_spin(&slab_lock);
  CONTAINER_FOREACH(slab_list, CLIST, &slab_root,
  {
    if (!memcmp(name, item->name) )
      return item;
  });
  lock_release(&slab_lock);
  return NULL;                 
}
  #endif /*CONFIG_MUTEK_SLAB_GLOBAL*/

/**
@this return the name of the given slab. The return string is a copy
and must be freed by calling function.
*/
static inline
uint8_t *slab_getname(struct slab_allocator_header_s* slab)
{
  return strdup(slab->name);
}
 #endif /*CONFIG_MUTEK_SLAB_NAME*/


#endif

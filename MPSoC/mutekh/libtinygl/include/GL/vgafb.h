
#ifndef _VGAFB_H_
#define _VGAFB_H_

#include <hexo/error.h>

/* for struct device_s definition */
#include <drivers/fb/vga/fb-vga.h>
#include <device/device.h>
#include <device/driver.h>

struct vgafb_context;

error_t vgafb_create_context(struct vgafb_context **ctx );

error_t vgafb_destroy_context(struct vgafb_context *ctx);

error_t vgafb_make_current(struct vgafb_context *ctx, struct device_s *fb_dev);

error_t vgafb_swap_buffer();

#endif

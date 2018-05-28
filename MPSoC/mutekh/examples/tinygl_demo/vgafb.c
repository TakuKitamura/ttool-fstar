
#include <pthread.h>
#include <mutek/printk.h>

/* for struct device_s definition */
#include <drivers/fb/vga/fb-vga.h>
#include <device/enum.h>
#include <device/device.h>
#include <device/driver.h>

#include <GL/gl.h>
#include <GL/vgafb.h>
#include "ui.h"

#if defined (CONFIG_ARCH_IBMPC)
extern struct device_s fb_dev;
#elif defined (CONFIG_ARCH_SOCLIB) && defined(CONFIG_ARCH_DEVICE_TREE)
struct device_s fdt_enum_dev;
#else
#error
#endif

struct device_s *tinygl_fb;

void tkSwapBuffers(void)
{
	vgafb_swap_buffer();
}

int ui_loop(int argc, char **argv, const char *name)
{
	struct vgafb_context *ctx = NULL;

	if (vgafb_create_context(&ctx) != 0)
        goto err;

#if defined (CONFIG_ARCH_IBMPC)
	/* can we assume dev_fb_init has already been called?
	 * 	(for now, we assume it is in hw_init.c) */
    tinygl_fb = &fb_dev;
#elif defined (CONFIG_ARCH_SOCLIB) && defined(CONFIG_ARCH_DEVICE_TREE)
    /* retrieve framebuffer from fdt */
    tinygl_fb = dev_enum_lookup(&fdt_enum_dev, "/fb@0");
    assert(tinygl_fb);
#endif

	/* setmode and ensure we are on the proper page */
	dev_fb_setmode(tinygl_fb, 320, 200, 8, FB_PACK_INDEX);
    dev_fb_flippage(tinygl_fb, 0);

	if (vgafb_make_current(ctx, tinygl_fb) != 0)
        goto err;

    init();
    printk("%lu cycles to init scene\n", cpu_cycle_count());

	reshape(320,200);

	while(1)
	{
        volatile cpu_cycle_t count;
        count = -cpu_cycle_count();

		/* we cannot handle key pressing */
		idle();

        count += cpu_cycle_count();
        printk("%lu cycles to display new frame\n", count);
	}

err:
    if (ctx != NULL)
        vgafb_destroy_context(ctx);
	return 0;
}

int main(int argc, char **argv);

void* demo_vgafb(void *param)
{
	main(0, NULL);

    return 0;
}

void app_start()
{
    static pthread_t a;
    pthread_create(&a, NULL, demo_vgafb, NULL);
}


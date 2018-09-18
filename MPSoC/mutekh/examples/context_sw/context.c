
#include <mutek/printk.h>
#include <hexo/context.h>

struct context_s ca;
struct context_s cb;

static CONTEXT_ENTRY(fa)
{
  while (1)
    {
      printk("Context A\n");
      context_switch_to(&cb);
    }
}

//static CONTEXT_ENTRY(fb)
static void fb(void *param)
{
  while (1)
    {
      printk("Context B\n");
      context_switch_to(&ca);
    }
}

unsigned char sa[1024];
unsigned char sb[1024];

void app_start()
{
  context_init(&ca, sa, sa + 1024, fa, 0);
  context_init(&cb, sb, sb + 1024, fb, 0);

  context_jump_to(&ca); // never returns
}


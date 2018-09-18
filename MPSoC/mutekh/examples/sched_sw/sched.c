
#include <mutek/printk.h>
#include <mutek/scheduler.h>

struct sched_context_s ca;
struct sched_context_s cb;

static CONTEXT_ENTRY(fa)
{
  while (1)
    {
      printk("Context A\n");
      sched_context_switch();
    }
}

static CONTEXT_ENTRY(fb)
{
  while (1)
    {
      printk("Context B\n");
      sched_context_switch();
    }
}

unsigned char sa[1024];
unsigned char sb[1024];

void app_start()
{
  context_init(&ca.context, sa, sa + 1024, fa, 0);
  sched_context_init(&ca);
  sched_context_start(&ca);

  context_init(&cb.context, sb, sb + 1024, fb, 0);
  sched_context_init(&cb);
  sched_context_start(&cb);

  return;
}


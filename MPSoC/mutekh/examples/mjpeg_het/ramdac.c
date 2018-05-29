
#include <mutek/printk.h>
#include "srl.h"
#include "mjpeg_tasks.h"

#include "jpeg_data.h"
#include "jpeg.h"

static uint32_t chksum = 0;
cpu_cycle_t last_frame = 0;
int id = 0;

void ramdac_func_bootstrap(struct _ramdac_args_t *_func_args)
{
}

void ramdac_func_ramdac(struct _ramdac_args_t *_func_args)
{
  cpu_cycle_t t;
  srl_mwmr_t input = _func_args->input;
  int32_t i, j;

  for (i = 0; i < MAX_HEIGHT; i += 8) {
    uint8_t row[MAX_WIDTH * 8];

    srl_mwmr_read( input, row, 8 * MAX_WIDTH );

    for (j = 0; j < MAX_WIDTH * 8; j++)
      chksum = (chksum << 1) + row[j];
  }

  t = cpu_cycle_count();
  printk("Image %i sum: %08x, cycle: %i\n", id++, chksum, (uint32_t)(t - last_frame));
  task_stats();
  last_frame = cpu_cycle_count();
}


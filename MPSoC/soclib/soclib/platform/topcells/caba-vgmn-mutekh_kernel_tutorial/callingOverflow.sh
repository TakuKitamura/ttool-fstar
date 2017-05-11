#!/bin/sh
awk -v sizeFIFO="$1" -f ~/TTool/MPSoC/soclib/soclib/platform/topcells/caba-vgmn-mutekh_kernel_tutorial/channelOverflow.awk ~/TTool/MPSoC/soclib/soclib/platform/topcells/caba-vgmn-mutekh_kernel_tutorial/mwmr0.log
echo "appel de mwmr0.log"
exit 0
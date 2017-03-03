/*****************************************************************************
  cavlc_tables.h -- File containing all the cavlc code tables

  Original Author: 
  Martin Fiedler, CHEMNITZ UNIVERSITY OF TECHNOLOGY, 2004-06-01	
  
  Thales Authors:
  Florian Broekaert, THALES COMMUNICATIONS - AAL, 2006
  Fabien Colas-Bigey, THALES COMMUNICATIONS - AAL, 2008

  Copyright (C) THALES & Martin Fiedler All rights reserved.
 
  This code is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by the
  Free Software Foundation, either version 3 of the License, or (at your
  option) any later version.
   
  This code is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
  for more details.
   
  You should have received a copy of the GNU General Public License along
  with this code (see file COPYING).  If not, see
  <http://www.gnu.org/licenses/>.
  
  This License does not grant permission to use the name of the copyright
  owner, except only as required above for reproducing the content of
  the copyright notice.
*****************************************************************************/

#ifndef __CAVLC_TABLES_H
#define __CAVLC_TABLES_H

/****************************************************************************
  Include section
****************************************************************************/
#include "cavlc.h"


/****************************************************************************
  Variables and structures
****************************************************************************/
#define COEFF_TOKEN(TrailingOnes,TotalCoeff) (((TotalCoeff)<<2)|(TrailingOnes))

static code_table_item CoeffTokenCodes[4][64]={ {
///// 0  <=  nC  <  2 /////
  { 0x00000000,  0, 0 },  // BOT
  { 0x00020000, 15, COEFF_TOKEN(1,13) },  // 0000 0000 0000 001
  { 0x00040000, 16, COEFF_TOKEN(0,16) },  // 0000 0000 0000 0100
  { 0x00050000, 16, COEFF_TOKEN(2,16) },  // 0000 0000 0000 0101
  { 0x00060000, 16, COEFF_TOKEN(1,16) },  // 0000 0000 0000 0110
  { 0x00070000, 16, COEFF_TOKEN(0,15) },  // 0000 0000 0000 0111
  { 0x00080000, 16, COEFF_TOKEN(3,16) },  // 0000 0000 0000 1000
  { 0x00090000, 16, COEFF_TOKEN(2,15) },  // 0000 0000 0000 1001
  { 0x000A0000, 16, COEFF_TOKEN(1,15) },  // 0000 0000 0000 1010
  { 0x000B0000, 16, COEFF_TOKEN(0,14) },  // 0000 0000 0000 1011
  { 0x000C0000, 16, COEFF_TOKEN(3,15) },  // 0000 0000 0000 1100
  { 0x000D0000, 16, COEFF_TOKEN(2,14) },  // 0000 0000 0000 1101
  { 0x000E0000, 16, COEFF_TOKEN(1,14) },  // 0000 0000 0000 1110
  { 0x000F0000, 16, COEFF_TOKEN(0,13) },  // 0000 0000 0000 1111
  { 0x00100000, 15, COEFF_TOKEN(3,14) },  // 0000 0000 0001 000
  { 0x00120000, 15, COEFF_TOKEN(2,13) },  // 0000 0000 0001 001
  { 0x00140000, 15, COEFF_TOKEN(1,12) },  // 0000 0000 0001 010
  { 0x00160000, 15, COEFF_TOKEN(0,12) },  // 0000 0000 0001 011
  { 0x00180000, 15, COEFF_TOKEN(3,13) },  // 0000 0000 0001 100
  { 0x001A0000, 15, COEFF_TOKEN(2,12) },  // 0000 0000 0001 101
  { 0x001C0000, 15, COEFF_TOKEN(1,11) },  // 0000 0000 0001 110
  { 0x001E0000, 15, COEFF_TOKEN(0,11) },  // 0000 0000 0001 111
  { 0x00200000, 14, COEFF_TOKEN(3,12) },  // 0000 0000 0010 00
  { 0x00240000, 14, COEFF_TOKEN(2,11) },  // 0000 0000 0010 01
  { 0x00280000, 14, COEFF_TOKEN(1,10) },  // 0000 0000 0010 10
  { 0x002C0000, 14, COEFF_TOKEN(0,10) },  // 0000 0000 0010 11
  { 0x00300000, 14, COEFF_TOKEN(3,11) },  // 0000 0000 0011 00
  { 0x00340000, 14, COEFF_TOKEN(2,10) },  // 0000 0000 0011 01
  { 0x00380000, 14, COEFF_TOKEN(1, 9) },  // 0000 0000 0011 10
  { 0x003C0000, 14, COEFF_TOKEN(0, 9) },  // 0000 0000 0011 11
  { 0x00400000, 13, COEFF_TOKEN(0, 8) },  // 0000 0000 0100 0
  { 0x00480000, 13, COEFF_TOKEN(2, 9) },  // 0000 0000 0100 1
  { 0x00500000, 13, COEFF_TOKEN(1, 8) },  // 0000 0000 0101 0
  { 0x00580000, 13, COEFF_TOKEN(0, 7) },  // 0000 0000 0101 1
  { 0x00600000, 13, COEFF_TOKEN(3,10) },  // 0000 0000 0110 0
  { 0x00680000, 13, COEFF_TOKEN(2, 8) },  // 0000 0000 0110 1
  { 0x00700000, 13, COEFF_TOKEN(1, 7) },  // 0000 0000 0111 0
  { 0x00780000, 13, COEFF_TOKEN(0, 6) },  // 0000 0000 0111 1
  { 0x00800000, 11, COEFF_TOKEN(3, 9) },  // 0000 0000 100
  { 0x00A00000, 11, COEFF_TOKEN(2, 7) },  // 0000 0000 101
  { 0x00C00000, 11, COEFF_TOKEN(1, 6) },  // 0000 0000 110
  { 0x00E00000, 11, COEFF_TOKEN(0, 5) },  // 0000 0000 111
  { 0x01000000, 10, COEFF_TOKEN(3, 8) },  // 0000 0001 00
  { 0x01400000, 10, COEFF_TOKEN(2, 6) },  // 0000 0001 01
  { 0x01800000, 10, COEFF_TOKEN(1, 5) },  // 0000 0001 10
  { 0x01C00000, 10, COEFF_TOKEN(0, 4) },  // 0000 0001 11
  { 0x02000000,  9, COEFF_TOKEN(3, 7) },  // 0000 0010 0
  { 0x02800000,  9, COEFF_TOKEN(2, 5) },  // 0000 0010 1
  { 0x03000000,  9, COEFF_TOKEN(1, 4) },  // 0000 0011 0
  { 0x03800000,  9, COEFF_TOKEN(0, 3) },  // 0000 0011 1
  { 0x04000000,  8, COEFF_TOKEN(3, 6) },  // 0000 0100
  { 0x05000000,  8, COEFF_TOKEN(2, 4) },  // 0000 0101
  { 0x06000000,  8, COEFF_TOKEN(1, 3) },  // 0000 0110
  { 0x07000000,  8, COEFF_TOKEN(0, 2) },  // 0000 0111
  { 0x08000000,  7, COEFF_TOKEN(3, 5) },  // 0000 100
  { 0x0A000000,  7, COEFF_TOKEN(2, 3) },  // 0000 101
  { 0x0C000000,  6, COEFF_TOKEN(3, 4) },  // 0000 11
  { 0x10000000,  6, COEFF_TOKEN(1, 2) },  // 0001 00
  { 0x14000000,  6, COEFF_TOKEN(0, 1) },  // 0001 01
  { 0x18000000,  5, COEFF_TOKEN(3, 3) },  // 0001 1
  { 0x20000000,  3, COEFF_TOKEN(2, 2) },  // 001
  { 0x40000000,  2, COEFF_TOKEN(1, 1) },  // 01
  { 0x80000000,  1, COEFF_TOKEN(0, 0) },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 2  <=  nC  <  4 /////
  { 0x00000000,  0, 0 },  // BOT
  { 0x00080000, 13, COEFF_TOKEN(3,15) },  // 0000 0000 0000 1
  { 0x00100000, 14, COEFF_TOKEN(3,16) },  // 0000 0000 0001 00
  { 0x00140000, 14, COEFF_TOKEN(2,16) },  // 0000 0000 0001 01
  { 0x00180000, 14, COEFF_TOKEN(1,16) },  // 0000 0000 0001 10
  { 0x001C0000, 14, COEFF_TOKEN(0,16) },  // 0000 0000 0001 11
  { 0x00200000, 14, COEFF_TOKEN(1,15) },  // 0000 0000 0010 00
  { 0x00240000, 14, COEFF_TOKEN(0,15) },  // 0000 0000 0010 01
  { 0x00280000, 14, COEFF_TOKEN(2,15) },  // 0000 0000 0010 10
  { 0x002C0000, 14, COEFF_TOKEN(1,14) },  // 0000 0000 0010 11
  { 0x00300000, 13, COEFF_TOKEN(2,14) },  // 0000 0000 0011 0
  { 0x00380000, 13, COEFF_TOKEN(0,14) },  // 0000 0000 0011 1
  { 0x00400000, 13, COEFF_TOKEN(3,14) },  // 0000 0000 0100 0
  { 0x00480000, 13, COEFF_TOKEN(2,13) },  // 0000 0000 0100 1
  { 0x00500000, 13, COEFF_TOKEN(1,13) },  // 0000 0000 0101 0
  { 0x00580000, 13, COEFF_TOKEN(0,13) },  // 0000 0000 0101 1
  { 0x00600000, 13, COEFF_TOKEN(3,13) },  // 0000 0000 0110 0
  { 0x00680000, 13, COEFF_TOKEN(2,12) },  // 0000 0000 0110 1
  { 0x00700000, 13, COEFF_TOKEN(1,12) },  // 0000 0000 0111 0
  { 0x00780000, 13, COEFF_TOKEN(0,12) },  // 0000 0000 0111 1
  { 0x00800000, 12, COEFF_TOKEN(0,11) },  // 0000 0000 1000
  { 0x00900000, 12, COEFF_TOKEN(2,11) },  // 0000 0000 1001
  { 0x00A00000, 12, COEFF_TOKEN(1,11) },  // 0000 0000 1010
  { 0x00B00000, 12, COEFF_TOKEN(0,10) },  // 0000 0000 1011
  { 0x00C00000, 12, COEFF_TOKEN(3,12) },  // 0000 0000 1100
  { 0x00D00000, 12, COEFF_TOKEN(2,10) },  // 0000 0000 1101
  { 0x00E00000, 12, COEFF_TOKEN(1,10) },  // 0000 0000 1110
  { 0x00F00000, 12, COEFF_TOKEN(0, 9) },  // 0000 0000 1111
  { 0x01000000, 11, COEFF_TOKEN(3,11) },  // 0000 0001 000
  { 0x01200000, 11, COEFF_TOKEN(2, 9) },  // 0000 0001 001
  { 0x01400000, 11, COEFF_TOKEN(1, 9) },  // 0000 0001 010
  { 0x01600000, 11, COEFF_TOKEN(0, 8) },  // 0000 0001 011
  { 0x01800000, 11, COEFF_TOKEN(3,10) },  // 0000 0001 100
  { 0x01A00000, 11, COEFF_TOKEN(2, 8) },  // 0000 0001 101
  { 0x01C00000, 11, COEFF_TOKEN(1, 8) },  // 0000 0001 110
  { 0x01E00000, 11, COEFF_TOKEN(0, 7) },  // 0000 0001 111
  { 0x02000000,  9, COEFF_TOKEN(3, 9) },  // 0000 0010 0
  { 0x02800000,  9, COEFF_TOKEN(2, 7) },  // 0000 0010 1
  { 0x03000000,  9, COEFF_TOKEN(1, 7) },  // 0000 0011 0
  { 0x03800000,  9, COEFF_TOKEN(0, 6) },  // 0000 0011 1
  { 0x04000000,  8, COEFF_TOKEN(0, 5) },  // 0000 0100
  { 0x05000000,  8, COEFF_TOKEN(2, 6) },  // 0000 0101
  { 0x06000000,  8, COEFF_TOKEN(1, 6) },  // 0000 0110
  { 0x07000000,  8, COEFF_TOKEN(0, 4) },  // 0000 0111
  { 0x08000000,  7, COEFF_TOKEN(3, 8) },  // 0000 100
  { 0x0A000000,  7, COEFF_TOKEN(2, 5) },  // 0000 101
  { 0x0C000000,  7, COEFF_TOKEN(1, 5) },  // 0000 110
  { 0x0E000000,  7, COEFF_TOKEN(0, 3) },  // 0000 111
  { 0x10000000,  6, COEFF_TOKEN(3, 7) },  // 0001 00
  { 0x14000000,  6, COEFF_TOKEN(2, 4) },  // 0001 01
  { 0x18000000,  6, COEFF_TOKEN(1, 4) },  // 0001 10
  { 0x1C000000,  6, COEFF_TOKEN(0, 2) },  // 0001 11
  { 0x20000000,  6, COEFF_TOKEN(3, 6) },  // 0010 00
  { 0x24000000,  6, COEFF_TOKEN(2, 3) },  // 0010 01
  { 0x28000000,  6, COEFF_TOKEN(1, 3) },  // 0010 10
  { 0x2C000000,  6, COEFF_TOKEN(0, 1) },  // 0010 11
  { 0x30000000,  5, COEFF_TOKEN(3, 5) },  // 0011 0
  { 0x38000000,  5, COEFF_TOKEN(1, 2) },  // 0011 1
  { 0x40000000,  4, COEFF_TOKEN(3, 4) },  // 0100
  { 0x50000000,  4, COEFF_TOKEN(3, 3) },  // 0101
  { 0x60000000,  3, COEFF_TOKEN(2, 2) },  // 011
  { 0x80000000,  2, COEFF_TOKEN(1, 1) },  // 10
  { 0xC0000000,  2, COEFF_TOKEN(0, 0) },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 4  <=  nC  <  8 /////
  { 0x00000000,  0, 0 },  // BOT
  { 0x00400000, 10, COEFF_TOKEN(0,16) },  // 0000 0000 01
  { 0x00800000, 10, COEFF_TOKEN(3,16) },  // 0000 0000 10
  { 0x00C00000, 10, COEFF_TOKEN(2,16) },  // 0000 0000 11
  { 0x01000000, 10, COEFF_TOKEN(1,16) },  // 0000 0001 00
  { 0x01400000, 10, COEFF_TOKEN(0,15) },  // 0000 0001 01
  { 0x01800000, 10, COEFF_TOKEN(3,15) },  // 0000 0001 10
  { 0x01C00000, 10, COEFF_TOKEN(2,15) },  // 0000 0001 11
  { 0x02000000, 10, COEFF_TOKEN(1,15) },  // 0000 0010 00
  { 0x02400000, 10, COEFF_TOKEN(0,14) },  // 0000 0010 01
  { 0x02800000, 10, COEFF_TOKEN(3,14) },  // 0000 0010 10
  { 0x02C00000, 10, COEFF_TOKEN(2,14) },  // 0000 0010 11
  { 0x03000000, 10, COEFF_TOKEN(1,14) },  // 0000 0011 00
  { 0x03400000, 10, COEFF_TOKEN(0,13) },  // 0000 0011 01
  { 0x03800000,  9, COEFF_TOKEN(1,13) },  // 0000 0011 1
  { 0x04000000,  9, COEFF_TOKEN(0,12) },  // 0000 0100 0
  { 0x04800000,  9, COEFF_TOKEN(2,13) },  // 0000 0100 1
  { 0x05000000,  9, COEFF_TOKEN(1,12) },  // 0000 0101 0
  { 0x05800000,  9, COEFF_TOKEN(0,11) },  // 0000 0101 1
  { 0x06000000,  9, COEFF_TOKEN(3,13) },  // 0000 0110 0
  { 0x06800000,  9, COEFF_TOKEN(2,12) },  // 0000 0110 1
  { 0x07000000,  9, COEFF_TOKEN(1,11) },  // 0000 0111 0
  { 0x07800000,  9, COEFF_TOKEN(0,10) },  // 0000 0111 1
  { 0x08000000,  8, COEFF_TOKEN(3,12) },  // 0000 1000
  { 0x09000000,  8, COEFF_TOKEN(2,11) },  // 0000 1001
  { 0x0A000000,  8, COEFF_TOKEN(1,10) },  // 0000 1010
  { 0x0B000000,  8, COEFF_TOKEN(0, 9) },  // 0000 1011
  { 0x0C000000,  8, COEFF_TOKEN(3,11) },  // 0000 1100
  { 0x0D000000,  8, COEFF_TOKEN(2,10) },  // 0000 1101
  { 0x0E000000,  8, COEFF_TOKEN(1, 9) },  // 0000 1110
  { 0x0F000000,  8, COEFF_TOKEN(0, 8) },  // 0000 1111
  { 0x10000000,  7, COEFF_TOKEN(0, 7) },  // 0001 000
  { 0x12000000,  7, COEFF_TOKEN(0, 6) },  // 0001 001
  { 0x14000000,  7, COEFF_TOKEN(2, 9) },  // 0001 010
  { 0x16000000,  7, COEFF_TOKEN(0, 5) },  // 0001 011
  { 0x18000000,  7, COEFF_TOKEN(3,10) },  // 0001 100
  { 0x1A000000,  7, COEFF_TOKEN(2, 8) },  // 0001 101
  { 0x1C000000,  7, COEFF_TOKEN(1, 8) },  // 0001 110
  { 0x1E000000,  7, COEFF_TOKEN(0, 4) },  // 0001 111
  { 0x20000000,  6, COEFF_TOKEN(0, 3) },  // 0010 00
  { 0x24000000,  6, COEFF_TOKEN(2, 7) },  // 0010 01
  { 0x28000000,  6, COEFF_TOKEN(1, 7) },  // 0010 10
  { 0x2C000000,  6, COEFF_TOKEN(0, 2) },  // 0010 11
  { 0x30000000,  6, COEFF_TOKEN(3, 9) },  // 0011 00
  { 0x34000000,  6, COEFF_TOKEN(2, 6) },  // 0011 01
  { 0x38000000,  6, COEFF_TOKEN(1, 6) },  // 0011 10
  { 0x3C000000,  6, COEFF_TOKEN(0, 1) },  // 0011 11
  { 0x40000000,  5, COEFF_TOKEN(1, 5) },  // 0100 0
  { 0x48000000,  5, COEFF_TOKEN(2, 5) },  // 0100 1
  { 0x50000000,  5, COEFF_TOKEN(1, 4) },  // 0101 0
  { 0x58000000,  5, COEFF_TOKEN(2, 4) },  // 0101 1
  { 0x60000000,  5, COEFF_TOKEN(1, 3) },  // 0110 0
  { 0x68000000,  5, COEFF_TOKEN(3, 8) },  // 0110 1
  { 0x70000000,  5, COEFF_TOKEN(2, 3) },  // 0111 0
  { 0x78000000,  5, COEFF_TOKEN(1, 2) },  // 0111 1
  { 0x80000000,  4, COEFF_TOKEN(3, 7) },  // 1000
  { 0x90000000,  4, COEFF_TOKEN(3, 6) },  // 1001
  { 0xA0000000,  4, COEFF_TOKEN(3, 5) },  // 1010
  { 0xB0000000,  4, COEFF_TOKEN(3, 4) },  // 1011
  { 0xC0000000,  4, COEFF_TOKEN(3, 3) },  // 1100
  { 0xD0000000,  4, COEFF_TOKEN(2, 2) },  // 1101
  { 0xE0000000,  4, COEFF_TOKEN(1, 1) },  // 1110
  { 0xF0000000,  4, COEFF_TOKEN(0, 0) },  // 1111
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 8  <=  nC /////
  { 0x00000000,  6, COEFF_TOKEN(0, 1) },  // 0000 00
  { 0x04000000,  6, COEFF_TOKEN(1, 1) },  // 0000 01
  { 0x0C000000,  6, COEFF_TOKEN(0, 0) },  // 0000 11
  { 0x10000000,  6, COEFF_TOKEN(0, 2) },  // 0001 00
  { 0x14000000,  6, COEFF_TOKEN(1, 2) },  // 0001 01
  { 0x18000000,  6, COEFF_TOKEN(2, 2) },  // 0001 10
  { 0x20000000,  6, COEFF_TOKEN(0, 3) },  // 0010 00
  { 0x24000000,  6, COEFF_TOKEN(1, 3) },  // 0010 01
  { 0x28000000,  6, COEFF_TOKEN(2, 3) },  // 0010 10
  { 0x2C000000,  6, COEFF_TOKEN(3, 3) },  // 0010 11
  { 0x30000000,  6, COEFF_TOKEN(0, 4) },  // 0011 00
  { 0x34000000,  6, COEFF_TOKEN(1, 4) },  // 0011 01
  { 0x38000000,  6, COEFF_TOKEN(2, 4) },  // 0011 10
  { 0x3C000000,  6, COEFF_TOKEN(3, 4) },  // 0011 11
  { 0x40000000,  6, COEFF_TOKEN(0, 5) },  // 0100 00
  { 0x44000000,  6, COEFF_TOKEN(1, 5) },  // 0100 01
  { 0x48000000,  6, COEFF_TOKEN(2, 5) },  // 0100 10
  { 0x4C000000,  6, COEFF_TOKEN(3, 5) },  // 0100 11
  { 0x50000000,  6, COEFF_TOKEN(0, 6) },  // 0101 00
  { 0x54000000,  6, COEFF_TOKEN(1, 6) },  // 0101 01
  { 0x58000000,  6, COEFF_TOKEN(2, 6) },  // 0101 10
  { 0x5C000000,  6, COEFF_TOKEN(3, 6) },  // 0101 11
  { 0x60000000,  6, COEFF_TOKEN(0, 7) },  // 0110 00
  { 0x64000000,  6, COEFF_TOKEN(1, 7) },  // 0110 01
  { 0x68000000,  6, COEFF_TOKEN(2, 7) },  // 0110 10
  { 0x6C000000,  6, COEFF_TOKEN(3, 7) },  // 0110 11
  { 0x70000000,  6, COEFF_TOKEN(0, 8) },  // 0111 00
  { 0x74000000,  6, COEFF_TOKEN(1, 8) },  // 0111 01
  { 0x78000000,  6, COEFF_TOKEN(2, 8) },  // 0111 10
  { 0x7C000000,  6, COEFF_TOKEN(3, 8) },  // 0111 11
  { 0x80000000,  6, COEFF_TOKEN(0, 9) },  // 1000 00
  { 0x84000000,  6, COEFF_TOKEN(1, 9) },  // 1000 01
  { 0x88000000,  6, COEFF_TOKEN(2, 9) },  // 1000 10
  { 0x8C000000,  6, COEFF_TOKEN(3, 9) },  // 1000 11
  { 0x90000000,  6, COEFF_TOKEN(0,10) },  // 1001 00
  { 0x94000000,  6, COEFF_TOKEN(1,10) },  // 1001 01
  { 0x98000000,  6, COEFF_TOKEN(2,10) },  // 1001 10
  { 0x9C000000,  6, COEFF_TOKEN(3,10) },  // 1001 11
  { 0xA0000000,  6, COEFF_TOKEN(0,11) },  // 1010 00
  { 0xA4000000,  6, COEFF_TOKEN(1,11) },  // 1010 01
  { 0xA8000000,  6, COEFF_TOKEN(2,11) },  // 1010 10
  { 0xAC000000,  6, COEFF_TOKEN(3,11) },  // 1010 11
  { 0xB0000000,  6, COEFF_TOKEN(0,12) },  // 1011 00
  { 0xB4000000,  6, COEFF_TOKEN(1,12) },  // 1011 01
  { 0xB8000000,  6, COEFF_TOKEN(2,12) },  // 1011 10
  { 0xBC000000,  6, COEFF_TOKEN(3,12) },  // 1011 11
  { 0xC0000000,  6, COEFF_TOKEN(0,13) },  // 1100 00
  { 0xC4000000,  6, COEFF_TOKEN(1,13) },  // 1100 01
  { 0xC8000000,  6, COEFF_TOKEN(2,13) },  // 1100 10
  { 0xCC000000,  6, COEFF_TOKEN(3,13) },  // 1100 11
  { 0xD0000000,  6, COEFF_TOKEN(0,14) },  // 1101 00
  { 0xD4000000,  6, COEFF_TOKEN(1,14) },  // 1101 01
  { 0xD8000000,  6, COEFF_TOKEN(2,14) },  // 1101 10
  { 0xDC000000,  6, COEFF_TOKEN(3,14) },  // 1101 11
  { 0xE0000000,  6, COEFF_TOKEN(0,15) },  // 1110 00
  { 0xE4000000,  6, COEFF_TOKEN(1,15) },  // 1110 01
  { 0xE8000000,  6, COEFF_TOKEN(2,15) },  // 1110 10
  { 0xEC000000,  6, COEFF_TOKEN(3,15) },  // 1110 11
  { 0xF0000000,  6, COEFF_TOKEN(0,16) },  // 1111 00
  { 0xF4000000,  6, COEFF_TOKEN(1,16) },  // 1111 01
  { 0xF8000000,  6, COEFF_TOKEN(2,16) },  // 1111 10
  { 0xFC000000,  6, COEFF_TOKEN(3,16) },  // 1111 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
} };


static code_table_item CoeffTokenCodes_ChromaDC[15]={
  { 0x00000000,  7, COEFF_TOKEN(3, 4) },  // 0000 000
  { 0x02000000,  8, COEFF_TOKEN(2, 4) },  // 0000 0010
  { 0x03000000,  8, COEFF_TOKEN(1, 4) },  // 0000 0011
  { 0x04000000,  7, COEFF_TOKEN(2, 3) },  // 0000 010
  { 0x06000000,  7, COEFF_TOKEN(1, 3) },  // 0000 011
  { 0x08000000,  6, COEFF_TOKEN(0, 4) },  // 0000 10
  { 0x0C000000,  6, COEFF_TOKEN(0, 3) },  // 0000 11
  { 0x10000000,  6, COEFF_TOKEN(0, 2) },  // 0001 00
  { 0x14000000,  6, COEFF_TOKEN(3, 3) },  // 0001 01
  { 0x18000000,  6, COEFF_TOKEN(1, 2) },  // 0001 10
  { 0x1C000000,  6, COEFF_TOKEN(0, 1) },  // 0001 11
  { 0x20000000,  3, COEFF_TOKEN(2, 2) },  // 001
  { 0x40000000,  2, COEFF_TOKEN(0, 0) },  // 01
  { 0x80000000,  1, COEFF_TOKEN(1, 1) },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
};


static code_table_item TotalZerosCodes_4x4[15][18]={ {
///// 1 /////
  { 0x00000000,  0, 0 },  // BOT
  { 0x00800000,  9, 15 },  // 0000 0000 1
  { 0x01000000,  9, 14 },  // 0000 0001 0
  { 0x01800000,  9, 13 },  // 0000 0001 1
  { 0x02000000,  8, 12 },  // 0000 0010
  { 0x03000000,  8, 11 },  // 0000 0011
  { 0x04000000,  7, 10 },  // 0000 010
  { 0x06000000,  7,  9 },  // 0000 011
  { 0x08000000,  6,  8 },  // 0000 10
  { 0x0C000000,  6,  7 },  // 0000 11
  { 0x10000000,  5,  6 },  // 0001 0
  { 0x18000000,  5,  5 },  // 0001 1
  { 0x20000000,  4,  4 },  // 0010
  { 0x30000000,  4,  3 },  // 0011
  { 0x40000000,  3,  2 },  // 010
  { 0x60000000,  3,  1 },  // 011
  { 0x80000000,  1,  0 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 2 /////
  { 0x00000000,  6, 14 },  // 0000 00
  { 0x04000000,  6, 13 },  // 0000 01
  { 0x08000000,  6, 12 },  // 0000 10
  { 0x0C000000,  6, 11 },  // 0000 11
  { 0x10000000,  5, 10 },  // 0001 0
  { 0x18000000,  5,  9 },  // 0001 1
  { 0x20000000,  4,  8 },  // 0010
  { 0x30000000,  4,  7 },  // 0011
  { 0x40000000,  4,  6 },  // 0100
  { 0x50000000,  4,  5 },  // 0101
  { 0x60000000,  3,  4 },  // 011
  { 0x80000000,  3,  3 },  // 100
  { 0xA0000000,  3,  2 },  // 101
  { 0xC0000000,  3,  1 },  // 110
  { 0xE0000000,  3,  0 },  // 111
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 3 /////
  { 0x00000000,  6, 13 },  // 0000 00
  { 0x04000000,  6, 11 },  // 0000 01
  { 0x08000000,  5, 12 },  // 0000 1
  { 0x10000000,  5, 10 },  // 0001 0
  { 0x18000000,  5,  9 },  // 0001 1
  { 0x20000000,  4,  8 },  // 0010
  { 0x30000000,  4,  5 },  // 0011
  { 0x40000000,  4,  4 },  // 0100
  { 0x50000000,  4,  0 },  // 0101
  { 0x60000000,  3,  7 },  // 011
  { 0x80000000,  3,  6 },  // 100
  { 0xA0000000,  3,  3 },  // 101
  { 0xC0000000,  3,  2 },  // 110
  { 0xE0000000,  3,  1 },  // 111
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 4 /////
  { 0x00000000,  5, 12 },  // 0000 0
  { 0x08000000,  5, 11 },  // 0000 1
  { 0x10000000,  5, 10 },  // 0001 0
  { 0x18000000,  5,  0 },  // 0001 1
  { 0x20000000,  4,  9 },  // 0010
  { 0x30000000,  4,  7 },  // 0011
  { 0x40000000,  4,  3 },  // 0100
  { 0x50000000,  4,  2 },  // 0101
  { 0x60000000,  3,  8 },  // 011
  { 0x80000000,  3,  6 },  // 100
  { 0xA0000000,  3,  5 },  // 101
  { 0xC0000000,  3,  4 },  // 110
  { 0xE0000000,  3,  1 },  // 111
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 5 /////
  { 0x00000000,  5, 11 },  // 0000 0
  { 0x08000000,  5,  9 },  // 0000 1
  { 0x10000000,  4, 10 },  // 0001
  { 0x20000000,  4,  8 },  // 0010
  { 0x30000000,  4,  2 },  // 0011
  { 0x40000000,  4,  1 },  // 0100
  { 0x50000000,  4,  0 },  // 0101
  { 0x60000000,  3,  7 },  // 011
  { 0x80000000,  3,  6 },  // 100
  { 0xA0000000,  3,  5 },  // 101
  { 0xC0000000,  3,  4 },  // 110
  { 0xE0000000,  3,  3 },  // 111
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 6 /////
  { 0x00000000,  6, 10 },  // 0000 00
  { 0x04000000,  6,  0 },  // 0000 01
  { 0x08000000,  5,  1 },  // 0000 1
  { 0x10000000,  4,  8 },  // 0001
  { 0x20000000,  3,  9 },  // 001
  { 0x40000000,  3,  7 },  // 010
  { 0x60000000,  3,  6 },  // 011
  { 0x80000000,  3,  5 },  // 100
  { 0xA0000000,  3,  4 },  // 101
  { 0xC0000000,  3,  3 },  // 110
  { 0xE0000000,  3,  2 },  // 111
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 7 /////
  { 0x00000000,  6, 9 },  // 0000 00
  { 0x04000000,  6, 0 },  // 0000 01
  { 0x08000000,  5, 1 },  // 0000 1
  { 0x10000000,  4, 7 },  // 0001
  { 0x20000000,  3, 8 },  // 001
  { 0x40000000,  3, 6 },  // 010
  { 0x60000000,  3, 4 },  // 011
  { 0x80000000,  3, 3 },  // 100
  { 0xA0000000,  3, 2 },  // 101
  { 0xC0000000,  2, 5 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 8 /////
  { 0x00000000,  6, 8 },  // 0000 00
  { 0x04000000,  6, 0 },  // 0000 01
  { 0x08000000,  5, 2 },  // 0000 1
  { 0x10000000,  4, 1 },  // 0001
  { 0x20000000,  3, 7 },  // 001
  { 0x40000000,  3, 6 },  // 010
  { 0x60000000,  3, 3 },  // 011
  { 0x80000000,  2, 5 },  // 10
  { 0xC0000000,  2, 4 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 9 /////
  { 0x00000000,  6, 1 },  // 0000 00
  { 0x04000000,  6, 0 },  // 0000 01
  { 0x08000000,  5, 7 },  // 0000 1
  { 0x10000000,  4, 2 },  // 0001
  { 0x20000000,  3, 5 },  // 001
  { 0x40000000,  2, 6 },  // 01
  { 0x80000000,  2, 4 },  // 10
  { 0xC0000000,  2, 3 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 10 /////
  { 0x00000000,  5, 1 },  // 0000 0
  { 0x08000000,  5, 0 },  // 0000 1
  { 0x10000000,  4, 6 },  // 0001
  { 0x20000000,  3, 2 },  // 001
  { 0x40000000,  2, 5 },  // 01
  { 0x80000000,  2, 4 },  // 10
  { 0xC0000000,  2, 3 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 11 /////
  { 0x00000000,  4, 0 },  // 0000
  { 0x10000000,  4, 1 },  // 0001
  { 0x20000000,  3, 2 },  // 001
  { 0x40000000,  3, 3 },  // 010
  { 0x60000000,  3, 5 },  // 011
  { 0x80000000,  1, 4 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 12 /////
  { 0x00000000,  4, 0 },  // 0000
  { 0x10000000,  4, 1 },  // 0001
  { 0x20000000,  3, 4 },  // 001
  { 0x40000000,  2, 2 },  // 01
  { 0x80000000,  1, 3 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 13 /////
  { 0x00000000,  3, 0 },  // 000
  { 0x20000000,  3, 1 },  // 001
  { 0x40000000,  2, 3 },  // 01
  { 0x80000000,  1, 2 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 14 /////
  { 0x00000000,  2, 0 },  // 00
  { 0x40000000,  2, 1 },  // 01
  { 0x80000000,  1, 2 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 15 /////
  { 0x00000000,  1, 0 },  // 0
  { 0x80000000,  1, 1 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
} };


static code_table_item TotalZerosCodes_ChromaDC[3][5]={ {
///// 1 /////
  { 0x00000000,  3, 3 },  // 000
  { 0x20000000,  3, 2 },  // 001
  { 0x40000000,  2, 1 },  // 01
  { 0x80000000,  1, 0 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 2 /////
  { 0x00000000,  2, 2 },  // 00
  { 0x40000000,  2, 1 },  // 01
  { 0x80000000,  1, 0 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 3 /////
  { 0x00000000,  1, 1 },  // 0
  { 0x80000000,  1, 0 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
} };


static code_table_item RunBeforeCodes[6][17]={ {
///// 1 /////
  { 0x00000000,  1, 1 },  // 0
  { 0x80000000,  1, 0 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 2 /////
  { 0x00000000,  2, 2 },  // 00
  { 0x40000000,  2, 1 },  // 01
  { 0x80000000,  1, 0 },  // 1
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 3 /////
  { 0x00000000,  2, 3 },  // 00
  { 0x40000000,  2, 2 },  // 01
  { 0x80000000,  2, 1 },  // 10
  { 0xC0000000,  2, 0 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 4 /////
  { 0x00000000,  3, 4 },  // 000
  { 0x20000000,  3, 3 },  // 001
  { 0x40000000,  2, 2 },  // 01
  { 0x80000000,  2, 1 },  // 10
  { 0xC0000000,  2, 0 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 5 /////
  { 0x00000000,  3, 5 },  // 000
  { 0x20000000,  3, 4 },  // 001
  { 0x40000000,  3, 3 },  // 010
  { 0x60000000,  3, 2 },  // 011
  { 0x80000000,  2, 1 },  // 10
  { 0xC0000000,  2, 0 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
},{
///// 6 /////
  { 0x00000000,  3, 1 },  // 000
  { 0x20000000,  3, 2 },  // 001
  { 0x40000000,  3, 4 },  // 010
  { 0x60000000,  3, 3 },  // 011
  { 0x80000000,  3, 6 },  // 100
  { 0xA0000000,  3, 5 },  // 101
  { 0xC0000000,  2, 0 },  // 11
  { 0xFFFFFFFF,  0, 0 }  // EOT
} };

#endif
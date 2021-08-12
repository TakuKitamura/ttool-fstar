module ValidatePacketData

open LowStar.BufferOps
open FStar.HyperStack.ST
open LowStar.Printf
open C.String

#set-options "--z3rlimit 1000 --max_fuel 0 --max_ifuel 0 --detail_errors"

module I8 = FStar.Int8
module I16 = FStar.Int16
module I32 = FStar.Int32
module I64 = FStar.Int64

module U8 = FStar.UInt8
module U16 = FStar.UInt16
module U32 = FStar.UInt32
module U64 = FStar.UInt64

module B = LowStar.Buffer

type struct_error = {
  code: I32.t;
  message: C.String.t;
}

let none_error: struct_error =
{
    code = 0l;
    message = !$"";
}

let ng_error: struct_error =
{
    code = 1l;
    message = !$"input is invalid range value";
}

noeq type fstar_uint8_array = {
    value: B.buffer U8.t;
    error: struct_error;
}

let ng_ret: fstar_uint8_array = {
    value = B.null;
    error = ng_error;
}

// let argConstraint can_id can_dlc frame_data  = (U8.eq (frame_data.(1ul)) 3uy)

let retConstraint can_id can_dlc frame_data ret ret_error = (true && I32.v ret_error.code = 0) || (I32.v ret_error.code > 0)

val validatePacketData_body:
  can_id: U32.t ->
  can_dlc: U8.t ->
  frame_data: B.buffer U8.t ->

Stack (ret: fstar_uint8_array) (requires fun h0 ->
   	((B.length frame_data) = (8)) &&
	(B.get h0 frame_data 1 = 3uy)
  )
  (ensures fun h0 ret h1 ->
    ((B.length frame_data) = (8)) &&
    retConstraint can_id can_dlc frame_data ret.value ret.error
  )
let validatePacketData_body can_id can_dlc frame_data  =
  {
      value = frame_data;
      error = none_error;
  }

val validatePacketData:
  can_id: U32.t ->
  can_dlc: U8.t ->
  frame_data: B.buffer U8.t ->

  Stack (ret: fstar_uint8_array) (requires fun h0 ->
    B.live h0 frame_data /\
    ((B.length frame_data) = (8)) /\
    (B.get h0 frame_data 1 = 3uy)
  )
  (ensures fun h0 ret h1 ->
    ((B.length frame_data) = (8)) &&
    retConstraint can_id can_dlc frame_data ret.value ret.error
  )
let validatePacketData can_id can_dlc frame_data  =
  if ((U8.eq (frame_data.(1ul)) 3uy)) then
    validatePacketData_body can_id can_dlc frame_data
  else
    ng_ret

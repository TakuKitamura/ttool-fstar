module Add

open LowStar.BufferOps
open FStar.HyperStack.ST
open LowStar.Printf
open C.String

module B = LowStar.Buffer
module U8 = FStar.UInt8
module U32 = FStar.UInt32
module I32 = FStar.Int32

let xrange x = I32.gte x 1l && I32.lte x 100l
let yrange y = I32.gte y 1l && I32.lte y 100l
let retRange ret = I32.gte ret 2l && I32.lte ret 200l

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

type struct_ret = {
    value: I32.t;
    error: struct_error;
}

let ng_ret: struct_ret = {
    value = 0l;
    error = ng_error;
}

val add_internal:
  x: I32.t{xrange x} ->
  y: I32.t{yrange y} ->
  ret: I32.t{retRange ret}
let add_internal x y = I32.add x y

val add: 
  x: I32.t ->
  y: I32.t ->
  ret: struct_ret{(retRange ret.value && I32.v ret.error.code = 0) || (I32.v ret.error.code > 0)}
let add x y = 
  if (xrange x) && (yrange y) then
    {
        value = add_internal x y;
        error = none_error;
    }
  else
    ng_ret
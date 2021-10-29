module HardCoding

open LowStar.BufferOps
open FStar.HyperStack.ST
open LowStar.Printf
open C.String

module I8 = FStar.Int8
module I16 = FStar.Int16
module I32 = FStar.Int32
module I64 = FStar.Int64

module U8 = FStar.UInt8
module U16 = FStar.UInt16
module U32 = FStar.UInt32
module U64 = FStar.UInt64

module B = LowStar.Buffer

noeq type struct_error = {
  code: I32.t;
  message: C.String.t;
}

noeq type fstar_integer a = {
    value: a;
    error: struct_error;
}

type fstar_int8 = fstar_integer I8.t
type fstar_int16 = fstar_integer I16.t
type fstar_int32 = fstar_integer I32.t
type fstar_int64 = fstar_integer I64.t

type fstar_uint8 = fstar_integer U8.t
type fstar_uint16 = fstar_integer U16.t
type fstar_uint32 = fstar_integer U32.t
type fstar_uint64 = fstar_integer U64.t

type fstar_int8_array = fstar_integer (B.buffer I8.t)
type fstar_int16_array = fstar_integer (B.buffer I16.t)
type fstar_int32_array = fstar_integer (B.buffer I32.t)
type fstar_int64_array = fstar_integer (B.buffer I64.t)

type fstar_uint8_array = fstar_integer (B.buffer U8.t)
type fstar_uint16_array = fstar_integer (B.buffer U16.t)
type fstar_uint32_array = fstar_integer (B.buffer U32.t)
type fstar_uint64_array = fstar_integer (B.buffer U64.t)
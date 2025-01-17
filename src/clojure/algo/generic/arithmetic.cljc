;; Generic interfaces for arithmetic operations

;; by Konrad Hinsen

;; Copyright (c) Konrad Hinsen, 2009-2011. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns
    ^{:author "Konrad Hinsen"
      :doc "Generic arithmetic interface
           This library defines generic versions of + - * / as multimethods
           that can be defined for any type. The minimal required
           implementations for a type are binary + and * plus unary - and /.
           Everything else is derived from these automatically. Explicit
           binary definitions for - and / can be provided for
           efficiency reasons."}
    clojure.algo.generic.arithmetic
    (:use [clojure.algo.generic
           :only (root-type nulary-type nary-type nary-dispatch)])
    (:refer-clojure :exclude [+ - * /]))


#?(:cljs (derive js/Number root-type))

;
; Universal zero and one values
;
(defrecord zero-type [])
(derive zero-type root-type)
(def zero (new zero-type))

(defrecord one-type [])
(derive one-type root-type)
(def one (new one-type))

;
; Addition
;
; The minimal implementation is for binary my-type. It is possible
; in principle to implement [::unary my-type] as well, though this
; doesn't make any sense.
;
(defmulti +
  "Return the sum of all arguments. The minimal implementation for type
   ::my-type is the binary form with dispatch value [::my-type ::my-type]."
  {:arglists '([x] [x y] [x y & more])}
  nary-dispatch)

(defmethod + nulary-type
  []
  zero)

(defmethod + root-type
  [x] x)

(defmethod + [root-type zero-type]
  [x y] x)

(defmethod + [zero-type root-type]
  [x y] y)

(defmethod + nary-type
  [x y & more]
  (if more
    (recur (+ x y) (first more) (next more))
    (+ x y)))

;
; Subtraction
;
; The minimal implementation is for unary my-type. A default binary
; implementation is provided as (+ x (- y)), but it is possible to
; implement unary my-type explicitly for efficiency reasons.
;
(defmulti -
  "Return the difference of the first argument and the sum of all other
   arguments. The minimal implementation for type ::my-type is the binary
   form with dispatch value [::my-type ::my-type]."
  {:arglists '([x] [x y] [x y & more])}
  nary-dispatch)

(defmethod - nulary-type
  []
  (throw (#?(:clj java.lang.IllegalArgumentException. :cljs js/Error.)
          "Wrong number of arguments passed")))

(defmethod - [root-type zero-type]
  [x y] x)

(defmethod - [zero-type root-type]
  [x y] (- y))

(defmethod - [root-type root-type]
  [x y] (+ x (- y)))

(defmethod - nary-type
  [x y & more]
  (if more
    (recur (- x y) (first more) (next more))
    (- x y)))

;
; Multiplication
;
; The minimal implementation is for binary [my-type my-type]. It is possible
; in principle to implement unary my-type as well, though this
; doesn't make any sense.
;
(defmulti *
  "Return the product of all arguments. The minimal implementation for type
   ::my-type is the binary form with dispatch value [::my-type ::my-type]."
  {:arglists '([x] [x y] [x y & more])}
  nary-dispatch)

(defmethod * nulary-type
  []
  one)

(defmethod * root-type
  [x] x)

(defmethod * [root-type one-type]
  [x y] x)

(defmethod * [one-type root-type]
  [x y] y)

(defmethod * nary-type
  [x y & more]
  (if more
    (recur (* x y) (first more) (next more))
    (* x y)))

;
;; Division
;;
;; The minimal implementation is for unary my-type. A default binary
;; implementation is provided as (* x (/ y)), but it is possible to
;; implement binary [my-type my-type] explicitly for efficiency reasons.

(defmulti /
  "Return the quotient of the first argument and the product of all other
   arguments. The minimal implementation for type ::my-type is the binary
   form with dispatch value [::my-type ::my-type]."
  {:arglists '([x] [x y] [x y & more])}
  nary-dispatch)

(defmethod / nulary-type
  []
  (throw (#?(:clj java.lang.IllegalArgumentException. :cljs js/Error.)
          "Wrong number of arguments passed")))

(defmethod / [root-type one-type]
  [x y] x)

(defmethod / [one-type root-type]
  [x y] (/ y))

(defmethod / [root-type root-type]
  [x y] (* x (/ y)))

(defmethod / nary-type
  [x y & more]
  (if more
    (recur (/ x y) (first more) (next more))
    (/ x y)))

#?(:clj
   (do
     (defmethod + [java.lang.Number java.lang.Number]
       [x y] (clojure.core/+ x y))

     (defmethod - java.lang.Number
       [x] (clojure.core/- x))

     (defmethod * [java.lang.Number java.lang.Number]
       [x y] (clojure.core/* x y))

     (defmethod / java.lang.Number
       [x] (clojure.core// x))))

#?(:cljs
   (do
     (defmethod + [js/Number js/Number]
       [x y] (cljs.core/+ x y))

     (defmethod - js/Number
       [x] (cljs.core/- x))

     (defmethod * [js/Number js/Number]
       [x y] (cljs.core/* x y))

     (defmethod / js/Number
       [x] (cljs.core// x))))

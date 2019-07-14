;; Generic interface for functors

;; by Konrad Hinsen

;; Copyright (c) Konrad Hinsen, 2009-2011. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns
    ^{:author "Konrad Hinsen, Pauli Jaakkola"
      :doc "Generic functor interface (fmap)"}
    clojure.algo.generic.functor)


(defmulti fmap
  "Applies function f to each item in the data structure s and returns
   a structure of the same kind."
  {:arglists '([f s])}
  (fn [f s] (if (fn? s)
              cljs.core/IFn
              (type s))))
; Using fn? is hacky, but unlike on the JVM (= (isa? IFn (type #())) false) and
; (like on the JVM) (type #()) doesn't return a type that would fit a defmethod.

(doseq [t [cljs.core/List
           cljs.core/Range
           cljs.core/IndexedSeq
           cljs.core/LazySeq]]
 (defmethod fmap t
   [f v]
   (map f v)))

(doseq [t [cljs.core/PersistentHashMap
           cljs.core/PersistentTreeMap
           cljs.core/PersistentArrayMap]]
  (defmethod fmap t
    [f m]
    (into (empty m) (for [[k v] m] [k (f v)]))))

(doseq [t [cljs.core/PersistentVector
           cljs.core/PersistentHashSet
           cljs.core/PersistentTreeSet]]
  (defmethod fmap t
    [f s]
    (into (empty s) (map f s))))

(defmethod fmap cljs.core/IFn
  [f fn]
  (comp f fn))

(prefer-method fmap cljs.core/PersistentVector cljs.core/IFn)
(prefer-method fmap cljs.core/PersistentHashMap cljs.core/IFn)
(prefer-method fmap cljs.core/PersistentTreeMap cljs.core/IFn)
(prefer-method fmap cljs.core/PersistentArrayMap cljs.core/IFn)
(prefer-method fmap cljs.core/PersistentHashSet cljs.core/IFn)
(prefer-method fmap cljs.core/PersistentTreeSet cljs.core/IFn)

(defmethod fmap cljs.core/Delay
  [f d]
  (delay (f @d)))

(defmethod fmap nil
  [_ _]
  nil)

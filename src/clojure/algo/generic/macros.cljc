(ns clojure.algo.generic.macros)

;;;
;;; Macros to permit access to the / multimethod via namespace qualification
;;;
(defmacro defmethod*
  "Define a method implementation for the multimethod name in namespace ns.
   Required for implementing the division function from another namespace."
  [ns name & args]
  (let [qsym (symbol (str ns) (str name))]
    `(defmethod ~qsym ~@args)))

(defmacro qsym
  "Create the qualified symbol corresponding to sym in namespace ns.
   Required to access the division function from another namespace,
   e.g. as (qsym clojure.algo.generic.arithmetic /)."
  [ns sym]
  (symbol (str ns) (str sym)))

;;; Taken from macrovich
(defmacro mcase [& {:keys [cljs clj]}]
  (if (contains? &env '&env)
    `(if (:ns ~'&env) ~cljs ~clj)
    (if #?(:clj (:ns &env) :cljs true)
      cljs
      clj)))

;;; One-argument math functions
(defmacro defmathfn-1
  [name]
  (let [sym (mcase :clj (symbol "java.lang.Math" (str name))
                   :cljs (symbol "js" (str "Math." name)))]
    `(do
       (defmulti ~name
         ~(str "Return the " name " of x.")
         {:arglists '([~'x])}
         type)
       (defmethod ~name (mcase :clj java.lang.Number :cljs js/Number)
         [~'x]
         (~sym ~'x)))))

;;; Two-argument math functions
(defmacro defmathfn-2
  [name]
  (let [sym (mcase :clj (symbol "java.lang.Math" (str name))
                   :cljs (symbol "js" (str "Math." name)))]
    `(do
       (defmulti ~name
         ~(str "Return the " name " of x and y.")
         {:arglists '([~'x ~'y])}
         (fn [x# y#] [(type x#) (type y#)]))
       (defmethod ~name (mcase :clj [java.lang.Number java.lang.Number]
                               :cljs [js/Number js/Number])
         [~'x ~'y]
         (~sym ~'x ~'y)))))

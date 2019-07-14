(ns clojure.algo.generic.test-runner
  (:require
   [clojure.algo.generic.test-collection]
   [clojure.algo.generic.test-comparison]
   [clojure.algo.generic.test-complex]
   [clojure.algo.generic.test-functor]
   #?(:clj [clojure.test :as t]
      :cljs [cljs.test :as t :include-macros true])))


(defn run-all
  []
  (t/run-tests 'clojure.algo.generic.test-collection
               'clojure.algo.generic.test-comparison
               'clojure.algo.generic.test-complex
               'clojure.algo.generic.test-functor
               ))

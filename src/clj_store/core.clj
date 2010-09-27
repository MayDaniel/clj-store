(ns clj-store.core
  (:use [clojure.contrib.def :only [defalias]])
  (:require [clojure.java.io :as io])
  (:import [java.io File Writer]))

(defn exists? [^File file]
  (.exists file))

(defn create
  "If strict is true, and the file has data not contained
   in a map, an empty map will replace it."
  [^File file ^Boolean strict]
  (when-not (or (exists? file)
                (and (true? strict)
                     (exists? file)
                     (-> file slurp read-string map?)))
    (spit file {})))

(defalias delete io/delete-file)

(defn init [^String name & {:keys [strict]}]
  (-> name io/file (create (true? strict))))

(defn in
  ([store] (do (init store) (load-file store)))
  ([store f] (f (in store)))
  ([store f & argseq] (apply f (in store) argseq)))

(defn out
  ([store f] (spit store (f (in store))))
  ([store f & argseq] (out store #(apply f % argseq))))

(defmacro with-store [store & body]
  `(spit ~store (-> (in ~store) ~@body)))

(defmethod print-method File [^File file ^Writer w]
  (.write w (str "#<File[" file "] " (slurp file) ">")))

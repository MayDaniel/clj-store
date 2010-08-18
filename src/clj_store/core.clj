(ns clj-store.core
  (:use [clojure.contrib.def :only [defalias]])
  (:require [clojure.java.io :as io])
  (:import [java.io File Writer]))

(defn exists? [^File file]
  (.exists file))

(defn create [^File file]
  (when-not (exists? file) (spit file {})))

(defalias delete io/delete-file)

(defn init [^String name]
  (-> name io/file create))

(defn in
  ([store] (do (init store) (load-file store)))
  ([store fun] (fun (in store)))
  ([store fun & argseq] (apply fun (in store) argseq)))

(defn out
  ([store fun] (split store (fun (in store))))
  ([store fun & argseq] (spit store (apply fun (in store) argseq))))

(defmacro with-out [store & body]
  `(spit ~store (-> (in ~store) ~@body)))

(defmethod print-method File [file ^Writer w]
  (.write w (slurp file)))

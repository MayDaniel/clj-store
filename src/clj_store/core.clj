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
  ([store fun] (fun (in store)))
  ([store fun & argseq] (apply fun (in store) argseq)))

(defn out
  ([store fun] (spit store (fun (in store))))
  ([store fun & argseq] (spit store (apply fun (in store) argseq))))

(defmacro with-out [store & body]
  `(spit ~store (-> (in ~store) ~@body)))

(defmethod print-method File [file ^Writer w]
  (.write w (slurp file)))

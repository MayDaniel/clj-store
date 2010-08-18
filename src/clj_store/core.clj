(ns clj-store.core
  (:require [clojure.java.io :as io])
  (:import [clojure.lang Counted Seqable Indexed]))

(defprotocol PStore
  (exists? [store])
  (create [store])
  (delete [store])
  (in [store] [store fun] [store fun argseq])
  (out [store fun] [store fun argseq]))

(deftype Store [^java.io.File store]
  Object
  (toString [_] (slurp store))
  Counted
  (count [this] (count (in this)))
  Seqable
  (seq [this] (seq (in this)))
  Indexed
  (nth [this index] (nth (-> this in seq) index))
  (nth [this index not-found] (nth (-> this in seq) index not-found))
  PStore
  (exists? [_] (.exists store))
  (create [this] (when-not (exists? this) (spit store {})))
  (delete [_] (io/delete-file store) nil)
  (in [_] (-> store slurp read-string))
  (in [this fun] (-> this in fun))
  (in [this fun argseq] (apply fun (in this) argseq))
  (out [this fun] (->> this in fun (spit store)))
  (out [this fun argseq] (spit store (apply fun (in this) argseq))))

(defn init-store [name]
  (let [s (Store. (io/file name))]
    (create s) s))

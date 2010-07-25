(ns clj-store.core
  (:use [clojure.contrib.duck-streams :only [spit slurp*]])
  (:import [java.io File]))

(defn new-store [file]
  (when-not (.exists (File. file))
    (.createNewFile (File. file))
    (spit file {})))

(defn read-store [file]
  (new-store file)
  (read-string (slurp* file)))

(defmacro with-store [file & body]
  `(spit ~file
         (-> (read-store file)
             ~@body)))

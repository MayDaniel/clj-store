(ns Clj-Store.core
  (:use [clojure.contrib.duck-streams :only [spit slurp*]])
  (:import java.io.File))

;; "s-" is used a prefix to avoid definition conflicts.
;; The paramater "file" may be absolute or relative.

(declare *store*)

(defn s-new
  "Creates a new store or file."
  [file]
  (when (false? (.exists (File. file)))
    (-> file File. .createNewFile)
    (spit file "")) file)

(defn s-read
  "Reads the map inside the store or a file."
  [file]
  (let [f (-> file slurp*)]
    (if (empty? f) {} (read-string f))))

(defmacro with-store
  "Creates - if one does not already exist - a new file, and evaluates
  the body."
  [file & body]
  `(binding [*store* (ref (s-read (s-new ~file)))]
     ~@body
     (spit ~file @*store*)))

(defn s-clean
  "Removes the contents of a store or a file."
  [file]
  (spit file ""))

(defn s-keys
  "Returns a sequence of the keys in the store."
  []	
  (-> @*store* s-read keys))

(defn s-vals
  "Returns a sequence of the values in the store."
  []
  (-> @*store* s-read vals))

(defn s-get
  "Returns the value of a single key."
  [key]
  (-> @*store* s-read key))

(defn s-get-in
  "See get-in."
  [ks]
  (reduce get (s-read @*store*) ks))

(defn s-assoc
  "Adds a single key to the store."
  [key val]
  (dosync (alter *store* assoc key val)))

(defn s-assoc-in
  "See assoc-in."
  [[k & ks] v]
  (let [key-args (into [k] ks)]
    (dosync (alter *store* merge (assoc-in @*store* key-args v)))))

(defn s-dissoc
  "Removes a single key from the store."
  [key]
  (dosync (alter *store* dissoc key)))

(defn s-add
  "Inserts new key/vals or updates the value of already existing ones
  an arbitrary number of key/vals."
  [hmap]  
  (dosync (alter *store* merge hmap)))

(defn s-update
  "Updates the value of keys already existing within the store."
  [hmap]
  (let [common-keys (ref (filter #(contains? @*store* (key %)) hmap))]
    (dosync (alter *store* merge @common-keys))))
